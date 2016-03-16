package it.gov.fermitivoli.server.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

/**
 * contiene l'ultimo token consumato
 */
@Deprecated
@Entity
public class GAE_Token_V2 implements Serializable {
    @Id
    public long id = 1;//id fisso
    public long currentToken;

    public GAE_Token_V2(long currentToken) {
        this.currentToken = currentToken;
        id = 1;
    }

    public GAE_Token_V2() {
        this.currentToken = 0;
        id = 1;

    }

    @Override
    public String toString() {
        return "GAE_Token_V2{" +
                "id=" + id +
                ", currentToken=" + currentToken +
                '}';
    }
}
