package com.crud.test;

import com.google.gson.JsonObject;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.SingleStreamCodeWriter;
import org.apache.commons.lang.WordUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.jsonschema2pojo.SchemaMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: hbhatia
 * Date: 6/2/15
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
// Generates classes in target folder inside the given package
// see https://github.com/joelittlejohn/jsonschema2pojo/wiki/Reference to create better schema files
public class GeneratePojo {

    public static String getClass(String metaJson, String className) throws IOException, URISyntaxException {
        JsonObject schema = createJsonSchemaFromInput(metaJson, className);
        return generatePojo(schema.toString());
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        JsonObject schema = createJsonSchemaFromInput(metaJson, "SampleClass");
        generatePojo(schema.toString());
    }

    public static JsonObject createJson(HashMap<String,Object> map, String className) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("$schema", "http://json-schema.org/draft-04/schema#");
        jsonObject.addProperty("additionalProperties", false);
        jsonObject.addProperty("type", "object");
        jsonObject.addProperty("javaType", WordUtils.capitalize(className));
        JsonObject properties = new JsonObject();
        Iterator<Map.Entry<String,Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            String name = entry.getKey();
            Map keyProperties = (Map)entry.getValue();
            String keyType = (String)keyProperties.get("type");

            if(keyType.equals("Date")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "string");
                keyDef.addProperty("format", "date-time");
                properties.add(name,keyDef);

            } else if(keyType.equals("String")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "string");
                properties.add(name,keyDef);

            } else if(keyType.equals("Long")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "string");
                keyDef.addProperty("format", "utc-millisec");
                properties.add(name,keyDef);

            } else if(keyType.contains("Map")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "object");
                String mapClass = String.format("java.util.Map<%s,%s>",
                        WordUtils.capitalize((String) keyProperties.get("mapKeyType")),
                        WordUtils.capitalize((String)keyProperties.get("mapValueType")));
                keyDef.addProperty("javaType", mapClass);
                properties.add(name,keyDef);

            } else if(keyType.equals("Double")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "string");
                keyDef.addProperty("format", "utc-millisec");
                properties.add(name,keyDef);

            } else if(keyType.equals("Integer")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "integer");
                properties.add(name,keyDef);

            } else if(keyType.equals("Double")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "number");
                properties.add(name,keyDef);

            } else if(keyType.equals("List")) {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "array");
                if(keyProperties.containsKey("listObjectTypeBasic")) {
                    JsonObject keyDef2 = new JsonObject();
                    keyDef2.addProperty("javaType", WordUtils.capitalize((String)keyProperties.get("listObjectTypeBasic")));
                    keyDef.add("items", keyDef2);
                } else if(keyProperties.containsKey("listObjectTypeOther")) {
                    JsonObject keyDef2 = new JsonObject();
                    keyDef2.addProperty("javaType",  WordUtils.capitalize((String)keyProperties.get("listObjectTypeOther")));
                    keyDef.add("items", keyDef2);
                }
                properties.add(name,keyDef);

            } else if(keyType.equals("OtherClass")){
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("javaType", WordUtils.capitalize((String)keyProperties.get("classname")));
                properties.add(name,keyDef);

            } else {
                JsonObject keyDef = new JsonObject();
                keyDef.addProperty("type", "Object");
                properties.add(name, keyDef);
            }
        }
        jsonObject.add("properties", properties);
        return jsonObject;
    }

    private static String generatePojo(String json) throws IOException, URISyntaxException {
        JCodeModel codeModel = new JCodeModel();
        //URL schemaContent = GeneratePojo.class.getResource("/schema/sample.json");
        new SchemaMapper().generate(codeModel, "SampleClass", "org.example", json);
        /*URL url = GeneratePojo.class.getResource("");
        System.out.println(url.getPath());*/
        /*codeModel.build(new File(System.getProperty("location")));*/
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        codeModel.build(new SingleStreamCodeWriter(os));
        String aString = new String(os.toByteArray(),"UTF-8");
        return aString;
    }


    public static JsonObject createJsonSchemaFromInput(String metaJson,  String className) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> map = mapper.readValue(metaJson, new TypeReference<HashMap<String,Object>>(){});
        JsonObject jsonObject = createJson(map, className);
        System.out.println(jsonObject.toString());
        return jsonObject;
    }

    public static String metaJson = "{\n" +
            "  \"createDate\" : {\"type\":\"Date\"},\n" +
            "  \"updateDate\" : {\"type\":\"Date\"},\n" +
            "  \"blahblah\" : {\"type\":\"Blah\"},\n" +
            "  \"statementId\": {\"type\":\"Long\"},\n" +
            "  \"verb\": {\"type\":\"OtherClass\", \"classname\":\"verb\"},\n" +
            "  \"completed\": {\"type\":\"enum\", \"enumarray\":[\"YES\",\"NO\"]},\n" +
            "  \"marks\" : {\"type\":\"Double\"},\n" +
            "  \"minutes\" : {\"type\":\"Integer\"},\n" +
            "  \"actor\" : {\"type\":\"List\", \"listObjectTypeOther\" : \"actor\"},\n" +
            "  \"authority\" : {\"type\":\"OtherClass\", \"classname\" : \"actor\"}\n" +
            "}";

    public static String metaOther = "{\n" +
            "  \"actor\" : {\n" +
            "    \"createDate\" : {\"type\" : \"Date\"},\n" +
            "    \"id\" : {\"type\" : \"Long\"},\n" +
            "    \"members\" : {\"type\" : \"List\", \"listObjectTypeOther\" : \"actor\"},\n" +
            "    \"account\" : {\"type\" : \"OtherClass\", \"classname\" : \"account\"},\n" +
            "    \"name\" : {\"type\" : \"string\"}\n" +
            "  },\n" +
            "  \"account\" : {\n" +
            "     \"homepage\" : {\"type\" : \"string\"},\n" +
            "     \"name\" : {\"type\" : \"string\"}\n" +
            "  },\n" +
            "  \"verb\" : {\n" +
            "     \"id\" : {\"type\" : \"string\"},\n" +
            "  \"display\" : {\"type\":\"Map\", \"mapKeyType\" : \"string\", \"mapValueType\" : \"string\"}\n" +
            "  }\n" +
            "}";

}
