package ui;

import javax.swing.table.AbstractTableModel;

/**
 * Created by kteng on 2014/12/21.
 */
public class TableModel extends AbstractTableModel{
    private Object[] columnNames = {"ip","role","status"};
    private Object[][] data = new Object[10][3];
 /*   @Override
    public int getRowCount() {
        return data.length;
    }*/

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return  data[rowIndex][columnIndex];
    }

    public void setValueAt(Object obj,int rowIndex,int columnIndex){
        data[rowIndex][columnIndex]=obj;
    }

    public int getRowCount(){
        for(int i=0;i<data.length;i++){
            Object[] objects = data[i];
            if(null==objects[0])
                return i;
        }
        return 0;
    }
}
