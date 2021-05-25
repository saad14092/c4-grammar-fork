package test.groovy

import static org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

import de.systemticks.c4.themes.model.ThemeLoader
import spock.lang.Specification

class C4ThemeSpec extends Specification {

	def "Testing if a theme object can be created from a json string" () {
		
		given: "A theme description as json file"
			def file = new File("./resource/themes/theme.json")
			def themeLoader = new ThemeLoader()
			
		when:
			def theme = themeLoader.loadFromFile(file)
		
		then:
			theme.name == "Amazon Web Services"
			theme.description == "This theme includes element styles with icons for each of the AWS services, based upon the AWS Architecture Icons (https://aws.amazon.com/architecture/icons/)."
			theme.elements.size() == 364
			
			theme.elements.get(0).tag == "Amazon Web Services - Kinesis Data Analytics"
			theme.elements.get(0).color == "#693cc5"  
			theme.elements.get(0).stroke == "#693cc5"
			theme.elements.get(0).icon == "https://static.structurizr.com/themes/amazon-web-services-2020.04.30/Amazon-Kinesis-Data-Analytics_light-bg@4x.png"						
	}
	
	def "Testing if a theme object can be created from an URL" () {
		
		given: "A theme description as URL"
			def url = new URL("https://gitlab.com/systemticks/c4-grammar/-/raw/master/c4-language-server/de.systemticks.c4.dsl.tests/resource/themes/theme.json")
			def themeLoader = new ThemeLoader()
			
		when:
			def theme = themeLoader.loadFromURL(url)
		
		then:
			theme.name == "Amazon Web Services"
			theme.description == "This theme includes element styles with icons for each of the AWS services, based upon the AWS Architecture Icons (https://aws.amazon.com/architecture/icons/)."
			theme.elements.size() == 364
			
			theme.elements.get(0).tag == "Amazon Web Services - Kinesis Data Analytics"
			theme.elements.get(0).color == "#693cc5"
			theme.elements.get(0).stroke == "#693cc5"
			theme.elements.get(0).icon == "https://static.structurizr.com/themes/amazon-web-services-2020.04.30/Amazon-Kinesis-Data-Analytics_light-bg@4x.png"
	}

	def "Testing if a theme object can be converted into a Map of theme elements" () {
		
		given: "A theme description as json file"
			def file = new File("./resource/themes/theme.json")
			def themeLoader = new ThemeLoader()
			def theme = themeLoader.loadFromFile(file)
			
		when:
			def themeMap = themeLoader.toMap(theme)
			def themeElement = themeMap.get("Amazon Web Services - Kinesis Data Analytics");
		
		then:
			themeMap.size() == 364
			
			themeElement.tag == "Amazon Web Services - Kinesis Data Analytics"
			themeElement.color == "#693cc5"
			themeElement.stroke == "#693cc5"
			themeElement.icon == "https://static.structurizr.com/themes/amazon-web-services-2020.04.30/Amazon-Kinesis-Data-Analytics_light-bg@4x.png"
	}

}
