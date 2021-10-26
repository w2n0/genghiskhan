package cn.w2n0.genghiskhan.utils.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * excel 文件存储工具
 * @author 无量

 */
@Slf4j
public class ExcelUtils {
    private static Map<String, Integer> getCellIndexs(Row rowheader) {
        int totalCells = rowheader.getPhysicalNumberOfCells();
        Map<String, Integer> filedIndexMap = new HashMap<>(10);
        for (int c = 0; c < totalCells; c++) {
            Cell cell = rowheader.getCell(c);
            HSSFDataFormatter hssfFdataFormatter = new HSSFDataFormatter();
            String cellValue = hssfFdataFormatter.formatCellValue(cell);
            if (!StringUtils.isEmpty(cellValue)) {
                filedIndexMap.put(cellValue, c);
            }
        }
        return filedIndexMap;
    }

    private static <T> List<T> getObjList(Map<String, List<KeyValue<Row, T>>> successRows) {
        List<T> objList = new ArrayList<T>();
        for (Map.Entry<String, List<KeyValue<Row, T>>> entry : successRows.entrySet()) {
            for (KeyValue<Row, T> obj : entry.getValue()) {
                objList.add(obj.getV());
            }
        }
        return objList;
    }

    private static <T> boolean isErrorOrder(List<KeyValue<Row, T>> list) {
        boolean iserr = false;
        for (KeyValue<Row, T> obj : list) {
            if (obj.getV() == null) {
                iserr = true;
                break;
            }
        }
        return iserr;
    }

    private static void delNullRows(Sheet sheet) {
        int i = sheet.getLastRowNum();
        Row tempRow;
        while (i > 0) {
            i--;
            tempRow = sheet.getRow(i);
            if (tempRow == null) {
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
            }
        }
    }
    private static <T> int getErrWorkbook(Map<String, List<KeyValue<Row, T>>> successRows) {
        int errcount = 0;
        List<T> objList = new ArrayList<T>();
        for (Iterator<Map.Entry<String, List<KeyValue<Row, T>>>> it = successRows.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<KeyValue<Row, T>>> item = it.next();
            boolean iserr = isErrorOrder(item.getValue());
            if (iserr) {
                //移除错误的记录
                it.remove();
                errcount = errcount + item.getValue().size();
            } else {
                //在excel中删除正确的记录,只保留错误的记录
                for (int j = 0; j < item.getValue().size(); j++) {
                    Row row = item.getValue().get(j).getK();
                    row.getSheet().removeRow(row);
                }
            }
        }
        return errcount;
    }

    public static <T> ExcelResult<T> adapter(Class<T> t, Workbook wb) throws IllegalAccessException, InstantiationException {
        ExcelResult<T> result = new ExcelResult<T>();
        Sheet sheet = wb.getSheetAt(0);
        Font font = wb.createFont();
        font.setColor(Font.COLOR_RED);
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(font);
        int totalRows = sheet.getLastRowNum();
        boolean sethint = false;
        if (totalRows >= 1) {
            Row rowheader = sheet.getRow(0);
            int lastCellNum = rowheader.getLastCellNum();
            Map<String, Integer> filedIndexMap = getCellIndexs(rowheader);
            Field[] fields = t.getDeclaredFields();
            Map<String, List<KeyValue<Row, T>>> successRows = new HashMap<>(10);
            for (int r = 1; r <= totalRows; r++) {
                Row row = sheet.getRow(r);
                T obj = t.newInstance();
                String primarykey = "";
                StringBuffer errorMsg = new StringBuffer();
                boolean error = false;
                if (row == null) {
                    continue;
                }
                for (Field field : fields) {
                    Column cell = field.getAnnotation(Column.class);
                    if (cell != null) {
                        String cellValue = null;
                        Object indexObj = filedIndexMap.get(cell.code());
                        if (indexObj != null) {
                            int index = (int) indexObj;
                            Cell cell1 = row.getCell(index);
                            HSSFDataFormatter hssfFdataFormatter = new HSSFDataFormatter();
                            cellValue = hssfFdataFormatter.formatCellValue(cell1);

                        }
                        if (cell.primarykey()) {
                            primarykey = primarykey + cellValue;
                        }
                        boolean success=(cell.primarykey() || cell.required()) &&
                                StringUtils.isEmpty(cellValue);
                        if (success) {
                            errorMsg.append(cell.code()).append(",不能为空;");
                            error = true;
                        } else if (!StringUtils.isEmpty(cellValue)) {
                            for (Regular regular : cell.regular()) {
                                if (!Pattern.compile(regular.value()).matcher(cellValue).matches()) {
                                    errorMsg.append(cell.code()).append(",应该包含").append(regular.toString());
                                    error = true;
                                }
                            }
                            for (Regular regular : cell.noRegular()) {
                                if (Pattern.compile(regular.value()).matcher(cellValue).matches()) {
                                    errorMsg.append(cell.code()).append(",不应该包含").append(regular.toString());
                                    error = true;
                                }
                            }
                            field.setAccessible(true);
                            Class<?> typeClass = field.getType();
                            if (String.class.isAssignableFrom(typeClass)) {
                                field.set(obj, cellValue);
                            } else if (Integer.class.isAssignableFrom(typeClass)) {
                                try {
                                    field.set(obj, Integer.valueOf(cellValue));
                                } catch (Exception x) {
                                    errorMsg.append(cell.code()).append(",格式错误应为数字");
                                    error = true;
                                }
                            } else if (BigDecimal.class.isAssignableFrom(typeClass)) {
                                try {
                                    field.set(obj, new BigDecimal(cellValue));
                                } catch (Exception x) {
                                    errorMsg.append(cell.code()).append(",格式错误应为数字");
                                    error = true;
                                }

                            } else if (Date.class.isAssignableFrom(typeClass)) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try {
                                    field.set(obj, sdf.parse(cellValue));
                                } catch (ParseException e) {
                                    errorMsg.append(cell.code()).append(",格式错误应为yyyy-MM-dd HH:mm:ss");
                                    error = true;
                                }
                            } else {
                                errorMsg.append(cell.code()).append(",格式错误,无法解析此格式;");
                                error = true;
                            }
                        }
                    }
                }
                KeyValue<Row, T> keyValue = new KeyValue<Row, T>();
                keyValue.setK(row);
                if (!error) {
                    keyValue.setV(obj);
                } else {
                    sethint = true;
                    Cell errorCell = row.createCell(lastCellNum);
                    errorCell.setCellValue(errorMsg.toString());
                    errorCell.setCellStyle(cellStyle);
                }
                List<KeyValue<Row, T>> list = successRows.get(primarykey);
                if (list == null) {
                    list = new ArrayList<KeyValue<Row, T>>();
                    list.add(keyValue);
                    successRows.put(primarykey, list);
                } else {
                    list.add(keyValue);
                }
            }
            if (sethint) {
                //如果存在失败的记录执行
                Cell errorMsgCell = rowheader.createCell(lastCellNum);
                sheet.setColumnWidth(lastCellNum, 30 * 256);
                errorMsgCell.setCellValue("提示");
                int errcount = getErrWorkbook(successRows);
                delNullRows(sheet);
                result.setErrWorkbook(wb);
                result.setFail(errcount);
            }
            List<T> objList = getObjList(successRows);
            result.setList(objList);
            result.setSuccess(objList.size());
        }
        return result;
    }

    public static <T> ExcelResult<T> adapter(Class<T> t, InputStream is) throws IOException, InvalidFormatException, InstantiationException, IllegalAccessException {
        Workbook wb = WorkbookFactory.create(is);
        return adapter(t, wb);
    }

    /**
     * @param filename "C:\\Users\\baby\\Downloads\\"+ UuidUtil.getUUID()+".xls"
     * @param workbook
     * @return
     */
    public static void toFile(String filename, Workbook workbook) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filename);
        workbook.write(fileOut);
        fileOut.close();
    }
}
