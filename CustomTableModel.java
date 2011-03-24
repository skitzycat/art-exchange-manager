import javax.swing.table.DefaultTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: test
 * Date: 3/21/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomTableModel extends DefaultTableModel {
    public boolean isCellEditable(int x, int y){
        return false;
    }
}
