package de.itemis.tooling.terminology.jsonexport

class JsonStringHelper {
	private new(){}

	def static String or(String primary, String secondary){
		if(!primary.nullOrEmpty) primary else secondary
	}

	def static String nlEspcape(String s, String nlReplace){
		s.replaceAll("<","&lt;").replaceAll("(\\s)*\r?\n(\\s)*",nlReplace)
	}

}