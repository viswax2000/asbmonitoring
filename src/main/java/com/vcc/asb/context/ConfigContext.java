package com.vcc.asb.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.vcc.asb.config.model.Resource;
import com.vcc.asb.metrics.command.Command;

@Component
public class ConfigContext extends AbstractContext {
	
	private Map<ConfigType, List<Command>> configCommands;
	private Map<ConfigType, List<Resource>> configurations;
	private Map<ConfigType, List<String>> configOutput;
	
	public ConfigContext() {
		
		this.configurations = new HashMap<ConfigType, List<Resource>>();
		this.configOutput = new HashMap<ConfigType, List<String>>();
		this.configCommands = new HashMap<ConfigType, List<Command>>();
		
		for(ConfigType c: ConfigType.values()) {
			this.configurations.put(c, new ArrayList<Resource>());
			this.configOutput.put(c, new ArrayList<String>());
			this.configCommands.put(c, new ArrayList<Command>());
		}
	}
	
	public List<Resource> getConfigTypeConfigurations(ConfigType ct) {
		return this.configurations.get(ct);
	}
	
	public void addConfiguration(Resource res) {
		
		ConfigType ct = ConfigType.getConfigType(res.getClass());
		
		if(ct!=null) {
			List<Resource> resourceConfig = this.configurations.get(ct);
			resourceConfig.add(res);
		}
	}
	
	public void addConfigTypeOutput(String output, ConfigType ct) {
		List<String> outputs = this.configOutput.get(ct);
		outputs.add(output);
	}
	
	public List<String> getConfigTypeOutput(ConfigType ct) {
		return this.configOutput.get(ct);
	}
	
	public List<Command> getConfigTypeCommands(ConfigType c) {
		List<Command> configTypeCommands = this.configCommands.get(c);
		return configTypeCommands;
	}
	
	public Map<ConfigType, List<Command>> getConfigCommands() {
		return this.configCommands;
	}
	
	public void addConfigTypeCommand(Command cmd, ConfigType ct) {
		List<Command> configTypeCommands = this.configCommands.get(ct);
		configTypeCommands.add(cmd);
	}
	
}
