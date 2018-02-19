package com.vcc.asb.util;

import java.util.HashMap;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class MonitorMetricsParser {
	
	public static HashMap<String, Integer> parseNamespaceMetrics(ObjectNode node) {
		
		HashMap<String, Integer> namespaceMetrics = new HashMap<String, Integer>();
		
		try {
		
			ObjectMapper mapper = new ObjectMapper();
			//Reader reader = new StringReader(jsonResponse);
			//ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
			//ObjectNode node = mapper.readValue(reader, ObjectNode.class);
			JsonNode valueNode = node.get("value");
			Properties props = new Properties();
			//System.out.println("ValueNode == null?"+(valueNode==null));
			if(valueNode!=null) {
				//System.out.println("ValueNode Size:"+valueNode.size());
			}
			
			
			for(int i=0;i<valueNode.size();++i) {
				JsonNode nameNode = valueNode.get(i).get("name");
				JsonNode metrics = nameNode.get("value");
				JsonNode timeseries = valueNode.get(i).get("timeseries");	//timeseries array
				JsonNode dataNode = null;
				
				//System.out.println("TimeSeries Node==null?"+(timeseries==null));
				if(timeseries!=null) {
					//System.out.println("Timeseries Size:"+timeseries.size());
				}
				
				if(timeseries.size()==0) {
					//empty data for this metrics
				} else {
					JsonNode dataNodes = timeseries.get(0).get("data");
					
					//System.out.println("dataNodes == null?"+(dataNodes==null));
					if(dataNodes!=null) {
						//System.out.println("dataNodes Size:"+dataNodes.size());
					}
					
					if(dataNodes.size()>0) {
						//System.out.println("Metrics:"+metrics.textValue());
						int sum = 0;
						
						for(int j=0;j<dataNodes.size();++j) {
							dataNode = dataNodes.get(j).get("total");
							
							//System.out.println("TotalNode == null?"+(dataNode==null));
							if(dataNode!=null) {
								//System.out.println("Total Value:"+dataNode.asText());
							}
							
							if(dataNode!=null) {
								String s = dataNode.asText();
								if(s.indexOf(".")!=-1) {
									s = s.substring(0, s.indexOf("."));
								} 
								int total = Integer.parseInt(s);
								//System.out.println("Total = "+total);
								sum += total;
							}
						}
						
						//System.out.println("Total "+metrics.textValue()+":"+sum);
						namespaceMetrics.put(metrics.textValue(), sum);
						
					}
				}
				props.setProperty(metrics.asText(), (dataNode!=null?dataNode.asText():""));
				
			}
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return namespaceMetrics;
		
	}

}
