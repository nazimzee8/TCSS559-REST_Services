package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.*;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Servlet implementation class assign3App
 */
@SuppressWarnings("unused")
@WebServlet("/")
public class WeightedSumModel extends HttpServlet {
	private static final long serialVersionUID = 1L;
	List<Double> weights;
	List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	List<Double> weightedSums = new ArrayList<Double>();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WeightedSumModel() {
        super();
    }
    
    public void getWeights(double w1, double w2, double w3, double w4, double w5) {
    	weights = new ArrayList<Double>(Arrays.asList(w1, w2, w3, w4, w5));
    	int sum = 0;
    	for (double w : weights) sum += w;
    	if (sum > Math.abs(1)) {
    		throw new RuntimeException("Sum of weights must be between -1 and 1!");
    	}
    }
    
    public String[] retrieveAttributes(HttpServletRequest request) {
    	return request.getParameterValues("attr");
    }
    
    public List<String> retrieveTypes(HttpServletRequest request) {
    	return new ArrayList<String>(Arrays.asList(request.getParameter("attrib1"), request.getParameter("attrib2"), request.getParameter("attrib3"), request.getParameter("attrib4"), request.getParameter("attrib5")));
    }
    
    public List<ArrayList<Double>> retrieveMatrix(HttpServletRequest request) {
    	String[] row1 = request.getParameterValues("row1");
    	String[] row2 = request.getParameterValues("row2");
    	String[] row3 = request.getParameterValues("row3");
    	String[] row4 = request.getParameterValues("row4");
    	String[] row5 = request.getParameterValues("row5");
    	
    	ArrayList<Double> raw1 = new ArrayList<Double>();
    	ArrayList<Double> raw2 = new ArrayList<Double>();
    	ArrayList<Double> raw3 = new ArrayList<Double>();
    	ArrayList<Double> raw4 = new ArrayList<Double>();
    	ArrayList<Double> raw5 = new ArrayList<Double>();
    	for (String r : row1) {
    		raw1.add(Double.parseDouble(r));
    	}
    	for (String r : row2) {
    		raw2.add(Double.parseDouble(r));
    	}
    	for (String r : row3) {
    		raw3.add(Double.parseDouble(r));
    	}
    	for (String r : row4) {
    		raw4.add(Double.parseDouble(r));
    	}
    	for (String r : row5) {
    		raw5.add(Double.parseDouble(r));
    	}
    	matrix = Arrays.asList(raw1, raw2, raw3, raw4, raw5);
    	return matrix;
    }
    
    public void normalizeMatrix() {
    	for (int i = 0; i < weights.size(); i++) {
			double sum = 0;
			for (int j = 0; j < matrix.size(); j++) {
				sum += matrix.get(j).get(i);
			}
			for (int j = 0; j < matrix.size(); j++) {
				double value = matrix.get(j).get(i);
				matrix.get(j).set(i, value / sum);
			}
		}
    }
    
    public void WeightedSum(HttpServletRequest request) {
		weightedSums = new ArrayList<Double>();
		List<String> types = this.retrieveTypes(request);
		List<Double> values = new ArrayList<Double>();
		for (String t : types) values.add(Double.parseDouble(t));
		for (int i = 0; i < matrix.size(); i++) {
			double sum = 0;
			for (int j = 0; j < weights.size(); j++) { 
				sum += (values.get(j) * weights.get(j) * matrix.get(i).get(j));
			}
			weightedSums.add(sum);
		}
	}
    
	public int preferredChoice() {
		int index = 0;
		//double max = 0;
		for (int i = 1; i < weightedSums.size(); i++) {
			if (weightedSums.get(i-1) <= weightedSums.get(i)) {
				index = i; 
				//max = weightedSums.get(i);
			}
		}
		return index;
	}
	
	public void BubbleSort(List<Double> weightedSums, String[] alternatives) {
        for (int i = 0; i < weightedSums.size(); i++) {
            for (int j = 0; j < weightedSums.size(); j++) {
                Double temp = weightedSums.get(j);
                String tempAlt = alternatives[j];
                if (j > 0 && temp > weightedSums.get(j - 1)) {
                    weightedSums.set(j, weightedSums.get(j-1));
                    alternatives[j] = alternatives[j-1];
                    weightedSums.set(j-1, temp);
                    alternatives[j-1] = tempAlt;
                }
            }
        }
    }
	
	public String getJSONOutput(String[] alternatives) throws JsonGenerationException, JsonMappingException, IOException {
		JsonObject json = Json.createObjectBuilder()
				/*.add("Alternatives", Json.createObjectBuilder()
					.add("First Alternative", alternatives[0])
					.add("Second Alternative", alternatives[1])
					.add("Third Alternative", alternatives[2])
					.add("Fourth Alternative" , alternatives[3])
					.add("Fifth Alternative", alternatives[4]))
				.add("WSM Scores", Json.createObjectBuilder()
					.add("First WSM Score", weightedSums.get(0))
					.add("Second WSM Score", weightedSums.get(1))
					.add("Third WSM Score", weightedSums.get(2))
					.add("Fourth WSM Score", weightedSums.get(3))
					.add("Fifth WSM Score", weightedSums.get(4)))*/
				.add("Descending Order", Json.createObjectBuilder()
					.add(alternatives[0], weightedSums.get(0))
					.add(alternatives[1], weightedSums.get(1))
					.add(alternatives[2], weightedSums.get(2))
					.add(alternatives[3], weightedSums.get(3))
					.add(alternatives[4], weightedSums.get(4)))
				.build();
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		Object jsonObject = mapper.readValue(json.toString(), Object.class);
		String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
		return result;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");           // set content-type to HTML 
        response.setHeader("restApp-version", "1.0");   // create custom header attribute for version 1.0 
        double w1 = Double.parseDouble(request.getParameter("w1"));
        double w2 = Double.parseDouble(request.getParameter("w2"));
        double w3 = Double.parseDouble(request.getParameter("w3"));
        double w4 = Double.parseDouble(request.getParameter("w4"));
        double w5 = Double.parseDouble(request.getParameter("w5"));
        this.getWeights(w1, w2, w3, w4, w5);
        this.retrieveMatrix(request);
        this.normalizeMatrix();
        this.WeightedSum(request);
        
        String[] alternatives = this.retrieveAttributes(request);
        BubbleSort(weightedSums, alternatives);
        
        //int option = this.preferredChoice();
        //String bestAlt = alternatives[option];
        //double score = weightedSums.get(option);
        
        //process weightedSums
        PrintWriter outputHTML = response.getWriter(); 
        //JsonReader jsonReader = Json.createReader();
        outputHTML.println("<html>"); 
        outputHTML.println("  <body>"); 
        outputHTML.println(this.getJSONOutput(alternatives));
        outputHTML.println(" </body>"); 
        outputHTML.println("</html>"); 
        outputHTML.close(); 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
