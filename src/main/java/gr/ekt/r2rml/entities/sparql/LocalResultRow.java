/**
 * Licensed under the Creative Commons Attribution-NonCommercial 3.0 Unported 
 * License (the "License"). You may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * 
 *  http://creativecommons.org/licenses/by-nc/3.0/
 *  
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.
 */
package gr.ekt.r2rml.entities.sparql;

import java.util.ArrayList;

/**
 * Holds information about a row in a SPARQL query resultset
 * @author nkons
 *
 */
public class LocalResultRow {

	private ArrayList<LocalResource> resources;
	
	/**
	 * 
	 */
	public LocalResultRow() {
	}
	
	/**
	 * @return the resources
	 */
	public ArrayList<LocalResource> getResources() {
		return resources;
	}
	/**
	 * @param resources the resources to set
	 */
	public void setResources(ArrayList<LocalResource> resources) {
		this.resources = resources;
	}
}
