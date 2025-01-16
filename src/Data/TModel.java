package Data;

import javax.swing.table.AbstractTableModel;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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

        // redovete
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
        Object value = row[columnIndex];


        if (columnIndex == 3 && value instanceof Date) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format((Date) value);
        }

        return value;
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