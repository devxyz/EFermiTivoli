package it.gov.fermitivoli.backoffice;

import it.gov.fermitivoli.backoffice.utilities.codegen.BatchCodeGenerator_forLayout;
import it.gov.fermitivoli.backoffice.utilities.greendao.GreenDaoGenerator;

/**
 * Created by stefano on 08/01/16.
 */
public class GenerateAllCode {
    public static void main(String[] args) throws Exception {
        BatchCodeGenerator_forLayout.main(args);
        GreenDaoGenerator.main(args);
    }
}
