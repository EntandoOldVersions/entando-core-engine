/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.entando.entando.aps.system.orm.util;

import com.agiletec.aps.system.ApsSystemUtils;
import com.agiletec.aps.system.exception.ApsSystemException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author E.Santoboni
 */
public class QueryExtractor {
	
	public static String[] extractQueries(String script) throws Throwable {
		if (null == script || script.trim().length() == 0) return null;
		String[] lines = readLines(script.trim());
        if (lines.length == 0) return null;
		return extractQueries(lines);
	}
	
	private static String[] readLines(String text) throws Throwable {
		InputStream is = null;
		String[] lines = new String[0];
		try {
			is = new ByteArrayInputStream(text.getBytes());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				lines = addChild(lines, strLine);
			}
		} catch (Throwable t) {
			ApsSystemUtils.logThrowable(t, QueryExtractor.class, "readLines", "Error reading lines");
			throw new ApsSystemException("Error reading lines", t);
		} finally {
			if (null != is) is.close();
		}
		return lines;
	}
	
	private static String[] extractQueries(String[] lines) {
		String[] queries = new String[0];
		StringBuilder builder = new StringBuilder();
		int length = lines.length;
		String lastValuedLine = null;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			builder.append(line);
			if (line.trim().length() > 0) lastValuedLine = line;
			if ((i+1) < length 
					&& lines[i+1].toLowerCase().trim().startsWith("insert into") 
					&& (null != lastValuedLine && lastValuedLine.toLowerCase().trim().endsWith(");"))) {
				String query = purgeQuery(builder);
				queries = addChild(queries, query);
				lastValuedLine = null;
			} else {
				//if (lines[i].trim().length() > 0) prev = lines[i];
				builder.append("\n");
			}
		}
		String query = purgeQuery(builder);
		queries = addChild(queries, query);
		return queries;
	}
	
	private static String purgeQuery(StringBuilder builder) {
		String query = builder.toString().trim();
		query = query.substring(0, query.length()-1);//cut ";"
		//query = this.insertQuotes(query);//inser quotes for column names into statement
		builder.delete(0, builder.length());
		return query;
	}
	/*
	private String insertQuotes(String query) {
		int start = query.indexOf("(");
		int end = query.indexOf(")");
		String section = query.substring(start+1, end);
		//System.out.println(section);
		String[] fields = section.split(",");
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < fields.length; i++) {
			if (i > 0) buffer.append(", ");
			String field = fields[i].trim();
			field = field.replaceAll("\"", "");
			buffer.append("\"").append(field.trim()).append("\"");
		}
		//System.out.println(buffer.toString());
		String newQuery = query.replaceFirst(section, buffer.toString());
		//System.out.println(newQuery);
		// TODO Auto-generated method stub
		return newQuery;
	}
	*/
	public static String[] addChild(String[] lines, String newLine) {
		int len = lines.length;
		String[] newChildren = new String[len + 1];
		for (int i = 0; i < len; i++) {
			newChildren[i] = lines[i];
		}
		newChildren[len] = newLine;
		return newChildren;
	}
	
}