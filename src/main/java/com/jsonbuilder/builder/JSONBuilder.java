package com.jsonbuilder.builder;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.HibernateAnnotationIntrospector;
import com.jsonbuilder.annotation.JSONNamedField;
import com.jsonbuilder.annotation.JSONNamedFields;

/**
 * Class responsible to build JSON objects.
 * 
 * HBM - Hibernate module that is provided to {@link ObjectMapper}
 * MAPPER - Object {@link ObjectMapper}
 * OBJECT_NODE - {@link JsonNode} specialization, to create leveled nodes.
 * 
 * @author Emmanuel Kiametis
 * @author Messias Xavier Sant'Ana
 * @author Hugo Arthur Amaral
 */
public class JSONBuilder {

	private final static Hibernate4Module HBM;
	private final static ObjectMapper MAPPER;

	private ObjectNode OBJECT_NODE;

	/**
	 * Initialize HBM and MAPPER.
	 */
	static {
		HBM = new Hibernate4Module();
		HBM.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
		MAPPER = new ObjectMapper();
		MAPPER.registerModule(HBM);
		// Configuring Jackson so that JsonNode can disable attributes on @javax.persistence.Transient
		MAPPER.setAnnotationIntrospector(new HibernateAnnotationIntrospector().setUseTransient(false));
	}

	/**
	 * Instantiate the class.
	 * 
	 * @return {@link JSONBuilder}
	 */
	public static JSONBuilder newInstance() {
		JSONBuilder INSTANCE = new JSONBuilder();
		INSTANCE.cleanBuilder();
		return INSTANCE;
	}

	/**
	 * Converts an {@link Object} to {@link JsonNode}.
	 * 
	 * @param object {@link Object}
	 * @return node {@link JsonNode}
	 */
	public static JsonNode toJSON(Object object) {
		JsonNode node = MAPPER.convertValue(object, JsonNode.class);
		return node;
	}

	/**
	 * Converts an {@link Object} to {@link JsonNode}.
	 * <br/>
	 * <b>If the field nameJsonField is not found, then the entire object is returned.</b>
	 * 
	 * @param object {@link Object}
	 * @param nameJsonField Name of the field. <b>The object must be annotated with @JSONNamedFields or @JSONNamedField.</b>
	 * @return {@link JsonNode}
	 */
	public static JsonNode toJSON(Object object, String nameJsonField) {
		JsonNode jsonNode = toJSON(object);
		verifyJsonFields(object, nameJsonField, jsonNode);
		verifyJsonField(object, nameJsonField, jsonNode);
		return jsonNode;
		
	}

	/**
	 * Checks if the object has {@link JSONNamedField} annotation.
	 * 
	 * @param object {@link Object}
	 * @param nameJsonField Name of the field. <b>The object must be annotated with @JSONNamedField.</b>
	 * @param jsonNode {@link JsonNode}
	 */
	private static void verifyJsonField(Object object, String nameJsonField, JsonNode jsonNode) {
		if(object.getClass().isAnnotationPresent(JSONNamedField.class)){
			JSONNamedField field = object.getClass().getAnnotation(JSONNamedField.class);
			if(field.name().equals(nameJsonField)){
				buildJson(jsonNode, field.fields());
			}
		}
	}

	/**
	 * Checks if the object has {@link JSONNamedFields} annotation.
	 * 
	 * @param object {@link Object}
	 * @param nameJsonField Name of the field. <b>The object must be annotated with @JSONNamedFields.</b>
	 * @param jsonNode {@link JsonNode}
	 */
	private static void verifyJsonFields(Object object, String nameJsonField, JsonNode jsonNode) {
		if(object.getClass().isAnnotationPresent(JSONNamedFields.class)){
			List<JSONNamedField> fields = Arrays.asList(object.getClass().getAnnotation(JSONNamedFields.class).value());
			for (JSONNamedField jsonField : fields) {
				if(jsonField.name().equals(nameJsonField)){
					buildJson(jsonNode, jsonField.fields());
				}
			}
		}
	}

	/**
	 * Build a {@link JsonNode} object.
	 * 
	 * @param jsonNode {@link JsonNode}
	 * @param fields - fields that won't be ignored. <b>THIS ARGUMENT IS OPTIONAL, HOWEVER IF THESE ARGUMENT IS NOT NULLL THE METHOD WILL IGNORE THE OTHER FIELDS.</b>
	 */
	@SuppressWarnings("unchecked")
	private static void buildJson(JsonNode jsonNode, String... fields) {
		List<String> acceptedFields = Arrays.asList(fields);
		if(acceptedFields != null && !acceptedFields.isEmpty()) {
			ObjectNode objectNode = (ObjectNode) jsonNode;
			List<String>arrayFields = IteratorUtils.toList(objectNode.fieldNames());
			for (String fieldName : arrayFields ) {
				if(!acceptedFields.contains(fieldName))
					objectNode.remove(fieldName);
			}
		}
	}

	/**
	 * Converts a {@link JsonNode} object to an {@link Object}.
	 * 
	 * @param node {@link JsonNode}
	 * @param clazz Class of the specify object.
	 * @return converted {@link Object}.
	 */
	public static <T> T fromJSON(JsonNode node, Class<T> clazz) {
		try {
			return MAPPER.treeToValue(node, clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a node into the tree of nodes, until the build using the method buildJson().
	 * 
	 * @param columnName Column name for the Json
	 * @param node of {@link JsonNode}
	 * @return The instance of this class {@link JSONBuilder}.
	 */
	public JSONBuilder addNode(String columnName, JsonNode node) {
		OBJECT_NODE.set(columnName, node);
		return this;
	}

	/**
	 * Build a {@link JsonNode} object from the nodes added by the call of the method addNode(String nomeColuna, JsonNode node).
	 * 
	 * @return node {@link JsonNode}
	 */
	public JsonNode buildJson() {
		ObjectNode resultado = OBJECT_NODE.deepCopy();
		cleanBuilder();
		return resultado;
	}

	/**
	 * Private method responsible for clean the instance after buildJson() is called.
	 */
	private void cleanBuilder() {
		OBJECT_NODE = MAPPER.createObjectNode();
	}

}
