
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class SensorName {
	public static String[] txt_to_arrayOfString() {
		ArrayList<String> name = new ArrayList<>();
		try {
			BufferedReader bf = new BufferedReader(new FileReader("C:/Users/Dandy/eclipse-workspace/UI/src/name/name_node.txt"));
			String line = null;
			while ((line = bf.readLine()) != null) {
				line = line.trim();
				if (!line.equals("")) {
					name.add(line);
				}
				if (name.size() == 46) {
					break;
				}
			}
			bf.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		String[] name_node = name.toArray(new String[name.size()]);
		return name_node;
	}
}
