package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public interface MCDA {
	
	public void retrieveMatrix(JsonObject data);
	public double[] retrieveTypes(JsonObject data);
	public String[] retrieveAlternatives(JsonObject data);
	public void normalizeMatrix();
	public void getWeights(JsonObject data);
	public void WeightedValues(JsonObject data);
	public void BubbleSort(List<Double> weightedValues, String[] alternatives);
	public String doScoring(HttpServletRequest request, HttpServletResponse response) throws JsonIOException, JsonSyntaxException, FileNotFoundException, JsonMappingException, JsonProcessingException;
	public JsonObject parseJSON(File file) throws JsonIOException, JsonSyntaxException, FileNotFoundException;
	public double getStdDeviation(List<Double> scores);
	public void setFile(File file);
	String getJSONOutput(String[] alternatives, List<Double> perfScores, String options)
			throws JsonMappingException, JsonProcessingException;
	public String retrieveOutput();
}
