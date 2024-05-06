package org.examples.drools.kafka.eventprocessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drlonyaml.model.DrlPackage;

import static org.drools.drlonyaml.model.Utils.getYamlMapper;

public class DrlToYamlConverter {

    public static void main(String[] args) {
        System.out.println(drl2yaml(new File("/home/mario/workspace/quarkus-drools-kafka/src/main/resources/org/examples/drools/kafka/eventprocessing/AlertingRules.drl")));
    }

    private static String drl2yaml(String drl) {
        return drl2yaml(new StringReader(drl));
    }

    private static String drl2yaml(File drlFile) {
        try {
            return drl2yaml(new FileReader(drlFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String drl2yaml(Reader drlReader) {
        try (StringWriter writer = new StringWriter()) {
            PackageDescr pkgDescr = new DrlParser().parse(drlReader);
            DrlPackage model = DrlPackage.from(pkgDescr);
            getYamlMapper().writeValue(writer, model);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
