package com.quant.core.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonParserUtils {

    public static <T> List<T> getJsonParserList(Class<T> target , JSONArray items){

        List<T> list = new ArrayList<>();

        for( Object o : items ){
            JSONObject item = (JSONObject) o;
            try {
                T obj = target.getDeclaredConstructor().newInstance();
                for (Field field : target.getDeclaredFields()){
                    field.setAccessible(true);
                    if( item.containsKey(field.getName())){
                        Object value = item.get(field.getName());

                        if(field.getType() == int.class ){
                            field.setInt(obj, (int)value);
                        }else if(field.getType() == double.class){
                            field.setDouble(obj, (double) value);
                        }else if(field.getType() == boolean.class){
                            field.setBoolean(obj, (boolean) value);
                        }else {
                            field.set(obj, value);
                        }
                    }
                    list.add(obj);
                }

            } catch (Exception e) {
                return Collections.emptyList();
            }
        }

        return list;
    }

    public static <T> T getJsonParser(Class<T> target , JSONObject item)  {
        try {
            T obj = target.getDeclaredConstructor().newInstance();
            for (Field field : target.getDeclaredFields()){
                field.setAccessible(true);
                if( item.containsKey(field.getName())){
                    Object value = item.get(field.getName());
                    if(field.getType() == int.class ){
                        field.setInt(obj, (int)value);
                    }else if(field.getType() == double.class){
                        field.setDouble(obj, (double) value);
                    }else if(field.getType() == boolean.class){
                        field.setBoolean(obj, (boolean) value);
                    }else {
                        field.set(obj, value);
                    }
                }
            }

            return obj;
        }catch (Exception e){
            return null;
        }
    }
}
