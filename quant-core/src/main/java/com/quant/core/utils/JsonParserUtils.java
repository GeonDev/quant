package com.quant.core.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonParserUtils {

    public <T> List<T> getJsonParserList(Class<T> target , JSONArray items, String prefix) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        if(items != null){
            for( Object o : items ){
                JSONObject item = (JSONObject) o;
                list.add(getJsonParser(target, item, prefix));
            }
        }
        return list;
    }

    public <T> T getJsonParser(Class<T> target , JSONObject item, String prefix) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        if(item != null){

                T obj = target.getDeclaredConstructor().newInstance();
                for (Field field : target.getDeclaredFields()){
                    //private 필드 수정 가능 처리
                    field.setAccessible(true);

                    if( item.containsKey(field.getName())){
                        Object value = item.get(field.getName());
                        if(field.getType() == int.class ){
                            field.setInt(obj, (int)value);
                        }else if(field.getType() == double.class){
                            field.setDouble(obj, (double) value);
                        }else if(field.getType() == boolean.class){
                            field.setBoolean(obj, (boolean) value);
                        }else if(field.getType() == LocalDateTime.class){
                            field.set(obj, DateUtils.toStringLocalDateTime((String)value));
                        }else if(field.getType() == long.class){
                            field.setLong(obj, (long) value);
                        }
                        else {
                            if(value instanceof JSONArray){
                                field.set(obj,getJsonParserList(Class.forName(capitalizeFirstLetter(field.getName(), prefix)) , (JSONArray) value, prefix));
                            }else if(value instanceof JSONObject){
                                field.set(obj,getJsonParser(Class.forName(capitalizeFirstLetter(field.getName(), prefix)) , (JSONObject) value, prefix));
                            }else {
                                field.set(obj, value);
                            }
                        }
                    }
                    field.setAccessible(false);
                }

                return obj;
        }

        return null;
    }

    private String capitalizeFirstLetter(String input, String prefix) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // 첫 번째 글자를 대문자로, 나머지 글자를 소문자로 변환
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.hasText(prefix) ? prefix : "");
        sb.append(StringUtils.hasText(input) ? input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase() : "");

        return sb.toString();
    }


}
