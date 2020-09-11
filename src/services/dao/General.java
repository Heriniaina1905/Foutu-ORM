package services.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Date; // Manalika raha lasa java.util.Date
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

/**
 * @author R. Heriniaina
 *
 */

public class General {
	
    /**
     * @param Object array
     * @param summing fields
     * @return sum of attribSommer value in the object array
     * @throws Exception
     */
    public Double sommeGeneral(Object[] tableau, String attribSommer) throws Exception {
        Class classe = tableau[0].getClass();
        Method methode = classe.getMethod("get" + attribSommer, null);
        Double result = new Double(0);
        for (int i = 0; i < tableau.length; i++) {
            result += (Double) methode.invoke(tableau[i], null);
        }
        return result;
    }

    /**
     * @param Table name
     * @param Package name
     * @param condition
     * @param services.dao.Connexion, connecting with Database
     * @return Data from database in Object Array
     * @throws Exception
     */
    public static Object[] select(String nt, String pckg, String condition, Connexion co) throws Exception {

        /**
         * Loading, Allocation and Preparation
         */
        boolean connect = false;
        String np = firstLetterToUpper(nt);
        np = pckg + "." + np;
        Class c = Class.forName(np);
        Object[] valinytemp = new Object[0];

        try {
            if (co == null) {
                co = new Connexion();
                connect = true;
            }
            String sql = "SELECT * FROM " + nt;
            if (condition.compareTo("") != 0) {
                sql += condition;
            }
            System.out.println(sql);
            ResultSet rs = co.getStatement().executeQuery(sql);

            int nbCol = rs.getMetaData().getColumnCount();
            
            Class[] types = getTypeField(c);
            
            Constructor constr = c.getConstructor(types);
            
            Object[] lesValAtt = new Object[nbCol];
            while (rs.next()) {
                for (int i = 0; i < nbCol; i++) {
                    String withGet = NameString(types[i].getName());
                    if (withGet.equals("int")) {
                        lesValAtt[i] = rs.getInt(i + 1);
                    }
                    if (withGet.equals("String")) {
                        lesValAtt[i] = rs.getString(i + 1);
                    }
                    if (withGet.equals("double")) {
                        Number temp = (Number) rs.getObject(i + 1);
                        lesValAtt[i] = temp.doubleValue();
                    }
                    if (withGet.equals("float")) {
                        Number temp = (Number) rs.getObject(i + 1);
                        lesValAtt[i] = temp.floatValue();
                    }
                    if (withGet.equals("Date")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        String dt = formatDt(rs.getString(i + 1));
                        java.util.Date utildate = formatter.parse(dt);
                        Date date = new Date(utildate.getTime());
                        lesValAtt[i] = date;
                    }
                    if (withGet.equals("boolean")) {
                        
                        lesValAtt[i] = rs.getBoolean(i + 1);
                     }
                }
                for(int indi = 0; indi < lesValAtt.length; indi+=1) {
                	System.out.println(lesValAtt[indi].toString());
                }
                Object o = constr.newInstance(lesValAtt);
                valinytemp = addObj(valinytemp, o);

            }
        } catch (Exception e) {
            if (connect == true) {
                co.clear();
            }
            throw e;
        } finally {
            if (connect == true) {
                co.clear();
            }
        }
        Object valiny = java.lang.reflect.Array.newInstance(c, valinytemp.length);
        for (int j = 0; j < valinytemp.length; j++) {
            java.lang.reflect.Array.set(valiny, j, valinytemp[j]);
        }
        return (Object[]) valiny;
    }

    /**
     * @param string
     * @return Up the first letter case
     */
    public static String firstLetterToUpper(String string) {
        String upperString = string.toUpperCase();
        String finalString = (upperString.compareTo(string) == 0)
                ? string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase()
                : string.substring(0, 1).toUpperCase() + string.substring(1);
        return finalString;
    }

    /**
     * @param object array
     * @param obj
     * @return object array
     * @description adding obj in object array
     */
    public static Object[] addObj(Object[] tab, Object obj) {
        int n = tab.length;
        Object[] res = new Object[n + 1];
        int i = 0;

        while (i < n) {
            res[i] = tab[i];
            i++;
        }
        res[n] = obj;

        return res;
    }

    /**
     * @param packAndClass
     * @return package.name
     */
    public static String NameString(String packAndClass) {
        int nb = packAndClass.lastIndexOf(".");
        if (nb > 0) {
            packAndClass = packAndClass.substring(nb + 1);
        }
        return packAndClass;
    }

    /**
     * @param class
     * @return type of class fields
     */
    public static Class[] getTypeField(Class c) {
        Field[] f = c.getDeclaredFields();
        Class[] types = new Class[f.length];
        for (int i = 0; i < f.length; i++) {
            types[i] = f[i].getType();
        }
        return types;
    }

    /**
     * @param class
     * @return getting table name near of the class
     */
    public static String getTab(Class c) {
        String val = c.getName();
        int i = val.lastIndexOf('.') + 1;
        val = val.substring(i);
        val = firstLetterToUpper(val);
        System.out.println(val);
        return val;
    }

    /**
     * @param o: Object
     * @param nt: Table Name
     * @throws Exception
     * @description : Insert object o in table nt
     */
    public static void insert(Object o, String nt) throws Exception {
        Connexion c = new Connexion();
        String sql = getSqlInsert(o, nt);
        System.out.println("sql ntsika : " + sql);
        ResultSet rs = c.getStatement().executeQuery(sql);
        rs = c.getStatement().executeQuery("commit");
        c.clear();
    }

    /**
     * @param obj
     * @param nt: Table name
     * @return SQL comand for inserting obj in table nt
     * @throws Exception
     */
    public static String getSqlInsert(Object obj, String nt) throws Exception {
        Class c = obj.getClass();
        String nomTab = getTab(c);
        if (nt != "") {
            nomTab = nt;
        }
        String sql = "insert into " + nomTab + " VALUES (";
        Field[] fs = c.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            String nm = firstLetterToUpper(fs[i].getName());
            if (nm.indexOf("_nit") == -1) {
                if (i != 0) {
                    sql += ",";
                }
                System.out.println(nm);
                System.out.println(fs[i].getType().getName());
                String col = nm;
                String nf = "get" + col;
                Method m = c.getMethod(nf);
                String val = m.invoke(obj).toString();
                String cls = c.getName().toUpperCase().substring(c.getPackage().getName().length() + 1,
                        c.getName().length());
                cls = cls + "_ID";
                String id = cls + ".NEXTVAL";
                System.out.println(c.getPackage().getName().toUpperCase() + "."
                        + fs[i].getName().substring(2).toUpperCase() + " = " + c.getName().toUpperCase());
                if (fs[i].getType() == val.getClass()) {
                    if (val.indexOf(".next") == -1) {
                        System.out.println("Condition voalohany");
                        // if(c.getPackage().getName().toUpperCase()+"."+fs[i].getName().substring(2).toUpperCase()
                        // == c.getName().toUpperCase()){
                        // System.out.println("Condition faharoa");
                        // val = id;
                        // // System.out.println(val);
                        // }
                        val = "'" + val + "'";
                    }
                }
                if (fs[i].getType().getName().equals("java.sql.Date")) {
                    val = "'" + formatDt(val) + "'";
                    System.out.println("Condition fahatelo");
                }
                if (fs[i].getType().getName().equals("Date")) {
                    val = "'" + formatDt(val) + "'";
                    System.out.println("Condition farany");
                }
                sql = sql + val;
            }
        }
        sql += ")";
        return sql;
    }

    /**
     * @param String date
     * @return date format dd - MM - YYYY
     * @throws Exception
     */
    public static String formatDt(String s) throws Exception {
        String j = " ", m = " ", a = " ";
        try {
            j = s.substring(8, 10);
            m = s.substring(5, 7);
            a = s.substring(0, 4);
        } catch (Exception e) {
            throw new Exception("date invalide <br>vous avez entree" + s);
        }
        return j + "-" + m + "-" + a;
    }

}
