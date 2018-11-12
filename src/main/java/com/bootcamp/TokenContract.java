package com.bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {

    public static String ID = "com.bootcamp.TokenContract";

    public interface Commands extends CommandData {
        class Issue implements Commands { }
    }

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        if (tx.getCommands().size() != 1)
            throw new IllegalArgumentException("Transaction must have exactly one command.");
        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandType = command.getValue();

        if (commandType instanceof Commands.Issue) {

            // Shape
            if (tx.getInputStates().size() != 0) throw new IllegalArgumentException("Transaction should have no inputs");
            if (tx.inputsOfType(TokenState.class).size() != 0) throw new IllegalArgumentException("Transaction should have no inputs");

            if (tx.outputsOfType(TokenState.class).size() < 1) throw new IllegalArgumentException("Transaction should have no inputs");
            if (tx.outputsOfType(TokenChildState.class).size() < 1) throw new IllegalArgumentException("Transaction should have no inputs");

            // Contents

            ContractState outputState = tx.getOutput(0);
            if (!(outputState instanceof TokenState) && !(outputState instanceof TokenChildState) ) throw new IllegalArgumentException("Transaction does not output a TokenState");

            // TODO - Implement check to make sure amount are positive
//
//            TokenChildState tokenChildState = null;
//            TokenState tokenState = null;
//
//            try {
//                tokenChildState = (TokenChildState) outputState;
//            } catch (Error err) {
//                tokenState = (TokenState) outputState;
//            }
//
//            if (tokenChildState != null) {
//
//                // Required Signers
//                Party owner = tokenState.getOwner();
//                PublicKey ownersKey = owner.getOwningKey();
//
//                Party issuer = tokenState.getIssuer();
//                PublicKey issuersKey = issuer.getOwningKey();
//
//                if (!(requiredSigners.contains(issuersKey))) throw new IllegalArgumentException("Transaction should be signed by the issuer");
//
//            } else {
//
//                // Required Signers
//                Party owner = tokenState.getOwner();
//                PublicKey ownersKey = owner.getOwningKey();
//
//                Party issuer = tokenState.getIssuer();
//                PublicKey issuersKey = issuer.getOwningKey();
//
//                if (!(requiredSigners.contains(issuersKey))) throw new IllegalArgumentException("Transaction should be signed by the issuer");
//
//            }


        } else {
            throw new IllegalArgumentException("Transaction command should be of type issue");
        }

    }
}