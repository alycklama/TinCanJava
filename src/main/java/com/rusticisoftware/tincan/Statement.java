/*
    Copyright 2013 Rustici Software

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.rusticisoftware.tincan;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;

import com.rusticisoftware.tincan.internal.DateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rusticisoftware.tincan.internal.StatementBase;
import com.rusticisoftware.tincan.json.StringOfJSON;

/**
 * Statement Class
 */
public class Statement extends StatementBase {
    private UUID id;    	
    private Agent authority;
    private TCAPIVersion version;

	public Statement() throws URISyntaxException, MalformedURLException {
		super();
	}
    
    @Deprecated
    private Boolean voided;

    public Statement(JsonNode jsonNode) throws URISyntaxException, MalformedURLException {
        super(jsonNode);

        JsonNode idNode = jsonNode.path("id");
        if (! idNode.isMissingNode()) {
            this.setId(UUID.fromString(idNode.textValue()));
        }

        JsonNode authorityNode = jsonNode.path("authority");
        if (! authorityNode.isMissingNode()) {
            this.setAuthority(Agent.fromJson(authorityNode));
        }
        
        JsonNode voidedNode = jsonNode.path("voided");
        if (! voidedNode.isMissingNode()) {
            this.setVoided(voidedNode.asBoolean());
        }
        
        JsonNode versionNode = jsonNode.path("version");
        if (! versionNode.isMissingNode()) {
            this.setVersion(TCAPIVersion.fromString(versionNode.textValue()));
        }
    }

    public Statement(StringOfJSON jsonStr) throws IOException, URISyntaxException {
        super(jsonStr);
    }

    public Statement(Agent actor, Verb verb, StatementTarget object, Result result, Context context) {
        super(actor, verb, object, result, context);
    }

    public Statement(Agent actor, Verb verb, StatementTarget object) {
        this(actor, verb, object, null, null);
    }

    @Override
    public ObjectNode toJSONNode(TCAPIVersion version) {
        ObjectNode node = super.toJSONNode(version);

        if (this.getId() != null) {
            node.put("id", this.getId().toString());
        }
        if (this.getStored() != null) {
            node.put("stored", this.getStored().toString());
        }
        if (this.getAuthority() != null) {
            node.put("authority", this.getAuthority().toJSONNode(version));
        }
        
        //Include 0.95 specific fields if asking for 0.95 version
        if (TCAPIVersion.V095.equals(version)) {
            if (this.getVoided() != null) {
                node.put("voided", this.getVoided());
            }
        }
        
        //Include 1.0.x specific fields if asking for 1.0.x version
        if (version.ordinal() <= TCAPIVersion.V100.ordinal()) {
            if (this.getVersion() != null) {
                node.put("version", this.getVersion().toString());
            }
        }
        
        return node;
    }

    /**
     * Method to set a random ID and the current date/time in the 'timestamp'
     */
    public void stamp() {
        if (this.getId() == null) {
            this.setId(UUID.randomUUID());
        }
        if (this.getTimestamp() == null) {
            this.setTimestamp(new DateTime());
        }
    }

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Agent getAuthority() {
		return authority;
	}

	public void setAuthority(Agent authority) {
		this.authority = authority;
	}

	public TCAPIVersion getVersion() {
		return version;
	}

	public void setVersion(TCAPIVersion version) {
		this.version = version;
	}

	public Boolean getVoided() {
		return voided;
	}

	public void setVoided(Boolean voided) {
		this.voided = voided;
	}
}
