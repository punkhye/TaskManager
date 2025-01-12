package Data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;


import javax.swing.table.AbstractTableModel;

public class TModel extends AbstractTableModel {

    private int rowCount;
    private final int columnCount;
    private final ArrayList<Object> data = new ArrayList<>();
    private final String[] columnNames;


    public TModel(ResultSet rs, String[] colsNames) throws Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        this.columnCount = metaData.getColumnCount();
        this.rowCount = 0;
        this.columnNames = colsNames;


        //dobavqne na data rows
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int j = 0; j < columnCount; j++) {
                row[j] = rs.getObject(j + 1);
            }
            data.add(row);
            rowCount++;
        }
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return columnCount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object[] row = (Object[]) data.get(rowIndex);
        return row[columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnNames != null && columnIndex < columnNames.length) {
            return columnNames[columnIndex];
        } else {
            return super.getColumnName(columnIndex);
        }
    }
}
