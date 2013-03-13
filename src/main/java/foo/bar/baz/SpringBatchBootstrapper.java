package foo.bar.baz;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import javax.sql.DataSource;

public class SpringBatchBootstrapper {

	public SpringBatchBootstrapper(DataSource dataSource) throws SQLException {
		bootstrap(dataSource);
	}

	private void bootstrap(DataSource dataSource) throws SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			String ddl = getSpringBatchDDL();
			connection = dataSource.getConnection();
			preparedStatement = connection.prepareStatement(ddl);
			preparedStatement.execute();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}
	}

	private String getSpringBatchDDL() throws IOException {
		URL url = getClass().getClassLoader().getResource(
				"org/springframework/batch/core/schema-h2.sql");

		StringBuilder sb = new StringBuilder();
		Scanner scanner = new Scanner(url.openStream(), "UTF-8");

		try {
			while (scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
				sb.append(System.getProperty("line.separator"));
			}
		} finally {
			scanner.close();
		}

		return sb.toString();
	}
}
