package com.dxc.batman.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Scenario directives. Dynamically imports the values explicit in application-profile.yml
 * 
 * @author marco.fioriti@dxc.com
 *
 */

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "direttiveconfig")
public class ScenarioDirective {
	private static Logger logger = LoggerFactory.getLogger(ScenarioDirective.class);
	
	private List<Scenario> scenari = new ArrayList<>();
	private String nota;
	private Map<String, Scenario> scenarioRefs;
	
	@PostConstruct
	public void loadDirective() {
		// Creation of reference scenarios to speed up subsequent searches 
		logger.debug("Loading Scenarios " + nota);
		scenarioRefs = new HashMap<String, Scenario>();
		for (Scenario scenario : scenari) {
			scenarioRefs.put(scenario.getNomescenario(), scenario);
			logger.debug("Loaded scenario: " + scenario.getNomescenario());
		}
	}
	
	public Scenario getScenario(String nomeScenario) {
		return scenarioRefs.get(nomeScenario);
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String n) {
		this.nota = n;
	}
	public List<Scenario> getScenari() {
		return scenari;
	}
}