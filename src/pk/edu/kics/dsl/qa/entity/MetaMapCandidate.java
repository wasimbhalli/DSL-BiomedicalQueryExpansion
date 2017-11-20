package pk.edu.kics.dsl.qa.entity;

import java.util.ArrayList;
import java.util.List;

public class MetaMapCandidate {
	ArrayList<String> prefered_name;
	ArrayList<String> semantic_type;
	ArrayList<String> candidateCUI;
	int resource_id;
	
	
	public MetaMapCandidate() {
		prefered_name = new ArrayList<>();
		semantic_type = new ArrayList<>();
		candidateCUI = new ArrayList<>();
		
	}
	
	public ArrayList<String> getPrefered_name() {
		return prefered_name;
	}
	public void setPrefered_name(ArrayList<String> prefered_name) {
		this.prefered_name = prefered_name;
	}
	public ArrayList<String> getSemantic_type() {
		return semantic_type;
	}
	public void setSemantic_type(ArrayList<String> semantic_type) {
		this.semantic_type = semantic_type;
	}
	public int getResource_id() {
		return resource_id;
	}
	public void setResource_id(int resource_id) {
		this.resource_id = resource_id;
	}
	public ArrayList<String> getCandidateCUI() {
		return candidateCUI;
	}
	public void setCandidateCUI(ArrayList<String> candidateCUI) {
		this.candidateCUI = candidateCUI;
	}
	

}
