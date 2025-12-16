

import org.junit.jupiter.api.Test;

import XMLIO.XMLAnalyser;
import metaModel.Model;
import prettyPrinter.PrettyPrinter;

class PrettyPrinterTest {

	@Test
	void test() {
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromFilenamed("src/main/resources/Exemple2.xml");
		PrettyPrinter pp = new PrettyPrinter();
		model.accept(pp);
		System.out.println(pp.result());
	}

}
