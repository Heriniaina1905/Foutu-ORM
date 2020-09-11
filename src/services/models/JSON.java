package services.models;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.json.JSONArray;
import org.json.JSONObject;

import services.dao.General;


public class JSON {
	@SuppressWarnings("null")
	public static JSONArray convertJSON(Object[] obj) throws SecurityException{
		Class cls = null;
		Field[] fields = null;
		Method[] getters = null;
		JSONArray result = new JSONArray();
		String valinyTemp = null;
		
		try {
			
			/// Getting getters
			for(int i = 0; i<obj.length; i+=1) {
				cls = obj[i].getClass();
				fields = cls.getDeclaredFields();
				getters = new Method[fields.length];
				JSONObject json = new JSONObject();
				System.out.println("============================");
				for(int j = 0; j<fields.length; j+=1) {
					if(fields[j].getName().indexOf("i") == 0 && fields[j].getName().indexOf("s") == 1) {
						getters[j] = cls.getMethod(fields[j].getName());
					}else {
						getters[j] = cls.getMethod("get"+General.firstLetterToUpper(fields[j].getName()));
					}
					System.out.println(i+": "+getters[j].getName());
					System.out.println("============================");
					System.out.println(i+": "+getters[j].invoke(obj[i]).toString());
					System.out.println("============================");
					
					/// Insertion des donnee das json
					switch(getters[j].getReturnType().toString()) {
						default:
							json.put(getters[j].getName(), getters[j].invoke(obj[i]).toString());
							break;
						case "int":
							json.put(getters[j].getName(), new Integer(getters[j].invoke(obj[i]).toString()).intValue());
							break;
						case "float":
							json.put(getters[j].getName(), new Float(getters[j].invoke(obj[i]).toString()).floatValue());
							break;
						case "double":
							json.put(getters[j].getName(), new Double(getters[j].invoke(obj[i]).toString()).doubleValue());
							break;
					}
					/// inserting data in JSONArray
					//result.put(json);
					result.put(i, json);
				}
				System.out.println("============================");
				System.out.println("============================");
				System.out.println("Test JSON");
				System.out.println(result.getJSONObject(i).get("getIdUsers").toString());
				
			}
			
		}catch(SecurityException se) {
			System.out.println("Security Exception!!!");
			se.printStackTrace();
		}catch(Exception e) {
			System.out.println("Autre Exception!!!");
			e.printStackTrace();
		}
		return result;
	}
}
