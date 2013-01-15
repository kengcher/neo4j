/**
 * Copyright (c) 2002-2013 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.kernel.impl.api;

import org.neo4j.kernel.api.KernelAPI;
import org.neo4j.kernel.api.TransactionContext;
import org.neo4j.kernel.impl.core.PropertyIndexManager;
import org.neo4j.kernel.impl.persistence.PersistenceManager;
import org.neo4j.kernel.impl.transaction.AbstractTransactionManager;
import org.neo4j.kernel.impl.transaction.LockManager;

public class Kernel implements KernelAPI
{
    private final AbstractTransactionManager transactionManager;
    private final PropertyIndexManager propertyIndexManager;
    private final PersistenceManager persistenceManager;
    private final LockManager lockManager;
    private final PersistenceCache cache;

    public Kernel( AbstractTransactionManager transactionManager, PropertyIndexManager propertyIndexManager,
            PersistenceManager persistenceManager, LockManager lockManager )
    {
        this.transactionManager = transactionManager;
        this.propertyIndexManager = propertyIndexManager;
        this.persistenceManager = persistenceManager;
        this.lockManager = lockManager;
        this.cache = new PersistenceCache( new TemporaryLabelAsPropertyLoader( persistenceManager ) );
    }
    
    @Override
    public TransactionContext newTransactionContext()
    {
        // I/O
        TransactionContext result = new TemporaryLabelAsPropertyTransactionContext( propertyIndexManager, persistenceManager );
        // + Locking
        result = new LockingTransactionContext( result, lockManager, transactionManager );
        // + Transaction life cycle
        result = new TransactionLifecycleTransactionContext( result, transactionManager, propertyIndexManager, persistenceManager, cache );
        // + Single statement at a time
        result = new SingleStatementTransactionContext( result );
        
        // done
        return result;
    }
}