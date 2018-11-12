package com.bootcamp;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/* Our flow, automating the process of updating the ledger.
 * See src/main/java/examples/ArtTransferFlowInitiator.java for an example. */

@InitiatingFlow
@StartableByRPC
public class TokenIssueFlow extends FlowLogic<SignedTransaction> {
    private final Party owner;
    private final int amount;

    public TokenIssueFlow(Party owner, int amount) {
        this.owner = owner;
        this.amount = amount;
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // We choose our transaction's notary (the notary prevents double-spends).
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        // We get a reference to our own identity.
        Party issuer = getOurIdentity();

        /* ============================================================================
         *         Create our TokenState to represent on-ledger tokens
         * ===========================================================================*/

        // Functioning loop to create child states.
        List<TokenChildState> listOfChildrenStates = new ArrayList<>();

        for (int count = 0; count <=5; count++) {
            TokenChildState child = new TokenChildState(owner, issuer, amount + 2, new UniqueIdentifier());
            listOfChildrenStates.add(child);
            List<MappedSchema> supportedSchemas = child.supportedSchemas();
        }

        TokenState tokenState = new TokenState(issuer, owner, amount, new UniqueIdentifier());

        // Example Code with child schemas being passed to the state
//        for (int count = 0; count <=5; count++) {
//            TokenChildState child = new TokenChildState(owner, issuer, amount + 2, new UniqueIdentifier());
//            listOfChildrenStates.add(child);
//            List<MappedSchema> supportedSchemas = child.supportedSchemas();
//            listOfChildrenSchemas.add(child.generateMappedObject(supportedSchemas.get(0)));
//        }
//
//        // We create our new TokenState.
//        TokenState tokenState = new TokenState(issuer, owner, amount, new UniqueIdentifier(), listOfChildrenSchemas);


        /* ============================================================================
         *      Build our token issuance transaction to update the ledger
         * ===========================================================================*/
        // We build our transaction.
        TransactionBuilder txBuilder = new TransactionBuilder();

        txBuilder.setNotary(notary);

        for (int count = 0; count < listOfChildrenStates.size(); count++) {
            TokenChildState child = listOfChildrenStates.get(count);
            txBuilder.addOutputState(child, TokenContract.ID);
        }

        txBuilder.addOutputState(tokenState, TokenContract.ID);

        TokenContract.Commands.Issue commandData = new TokenContract.Commands.Issue();
        List<PublicKey> requiredSigners = ImmutableList.of(issuer.getOwningKey());
        txBuilder.addCommand(commandData, requiredSigners);

        /* ============================================================================
         *          Write our TokenContract to control token issuance!
         * ===========================================================================*/
        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(txBuilder);

        // We check our transaction is valid based on its contracts.
        txBuilder.verify(getServiceHub());

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(signedTransaction));
    }
}