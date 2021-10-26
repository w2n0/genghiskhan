package cn.w2n0.genghiskhan.utils.excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * Excel返回结果
 * @author 无量
 */
public class ExcelResult<T> {
    List<T> list;
    int success;
    int fail;
    Workbook errWorkbook;
    public List<T> getList () {
        return list;
    }
    public void setList (List<T> list) {
        this.list = list;
    }
    public int getSuccess () {
        return success;
    }

    public void setSuccess (int success) {
        this.success = success;
    }

    public int getFail () {
        return fail;
    }

    public void setFail (int fail) {
        this.fail = fail;
    }

    public Workbook getErrWorkbook () {
        return errWorkbook;
    }

    public void setErrWorkbook (Workbook errWorkbook) {
        this.errWorkbook = errWorkbook;
    }
}
