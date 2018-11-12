package com.bootcamp.schema;

import com.bootcamp.TokenState;
import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.serialization.CordaSerializable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An IOUState schema.
 */

@CordaSerializable
public class TokenSchemaV1 extends MappedSchema {

    public TokenSchemaV1() {
        super(TokenSchema.class, 1, ImmutableList.of(PersistentToken.class, PersistentChildToken.class));
    }

    @Entity
    @Table(name = "token_states")
    public static class PersistentToken extends PersistentState {
        @Column(name = "owner") private final String owner;
        @Column(name = "issuer") private final String issuer;
        @Column(name = "amount") private final int amount;
        @Column(name = "linear_id") private final UUID linearId;
        @OneToMany(mappedBy = "persistentToken") private final List<PersistentChildToken> childTokens;
        //get() = field

        public PersistentToken(String owner, String issuer, int amount, UUID linearId) {
            this.owner = owner;
            this.issuer = issuer;
            this.amount = amount;
            this.linearId = linearId;
            this.childTokens = new ArrayList<>();
        }

        // Default constructor required by hibernate.
        public PersistentToken() {
            this.owner = "";
            this.issuer = "";
            this.amount = 0;
            this.linearId = UUID.randomUUID();
            this.childTokens = null;
        }

        public String getOwner() {
            return owner;
        }

        public String getIssuer() {
            return issuer;
        }

        public int getAmount() {
            return amount;
        }

        public UUID getLinearId() {
            return linearId;
        }

        public List<PersistentChildToken> getChildTokens() { return childTokens; }
    }

    @Entity
    @Table(name = "token_child_states")
    public static class PersistentChildToken extends PersistentState {
        @Column(name = "owner")
        private final String owner;
        @Column(name = "issuer")
        private final String issuer;
        @Column(name = "amount")
        private final int amount;
        @Column(name = "child_proof")
        private final String childProof;
        @ManyToOne(targetEntity = PersistentToken.class)
        private final TokenState persistentToken;

        public PersistentChildToken(String owner, String issuer, int amount) {
            this.owner = owner;
            this.issuer = issuer;
            this.amount = amount;
            this.persistentToken = null;
            this.childProof = "I am a child";
        }

        // Default constructor required by hibernate.
        public PersistentChildToken() {
            this.owner = "";
            this.issuer = "";
            this.amount = 0;
            this.persistentToken = null;
            this.childProof = "I am a child";
        }

        public String getOwner() {
            return owner;
        }

        public String getIssuer() {
            return issuer;
        }

        public int getAmount() {
            return amount;
        }

        public TokenState getPersistentToken() {
            return persistentToken;
        }
    }
}
