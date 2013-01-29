/*
 * This software was developed at the National Institute of Standards and
 * Technology by employees of the Federal Government in the course of
 * their official duties. Pursuant to title 17 Section 105 of the United
 * States Code this software is not subject to copyright protection and is
 * in the public domain. This software is an experimental system. NIST assumes
 * no responsibility whatsoever for its use by other parties, and makes no
 * guarantees, expressed or implied, about its quality, reliability, or
 * any other characteristic. We would appreciate acknowledgement if the
 * software is used.
 */
package edu.illinois.ncsa.versus.web.server.dispatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.tupeloproject.kernel.BeanSession;
import org.tupeloproject.kernel.OperatorException;
import org.tupeloproject.kernel.Unifier;
import org.tupeloproject.rdf.ObjectResourceMapping;
import org.tupeloproject.rdf.Resource;
import org.tupeloproject.rdf.terms.Dc;
import org.tupeloproject.rdf.terms.DcTerms;
import org.tupeloproject.rdf.terms.Rdf;
import org.tupeloproject.util.Table;
import org.tupeloproject.util.Tuple;

import edu.illinois.ncsa.mmdb.web.client.dispatch.ListQueryResult;
import edu.illinois.ncsa.mmdb.web.server.Memoized;
import edu.illinois.ncsa.mmdb.web.server.TupeloStore;
import edu.illinois.ncsa.versus.web.client.dispatch.ListQueryCollections;
import edu.uiuc.ncsa.cet.bean.CollectionBean;
import edu.uiuc.ncsa.cet.bean.tupelo.CollectionBeanUtil;
import edu.uiuc.ncsa.cet.bean.tupelo.PersonBeanUtil;
import edu.uiuc.ncsa.cet.bean.tupelo.mmdb.MMDB;
import net.customware.gwt.dispatch.server.ActionHandler;
import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.ActionException;

/**
 *
 * @author antoinev
 */
public class ListQueryCollectionsHandler implements
        ActionHandler<ListQueryCollections, ListQueryResult<CollectionBean>> {

    private static final Logger log = Logger.getLogger(ListQueryCollectionsHandler.class.getName());

    @Override
    public ListQueryResult<CollectionBean> execute(ListQueryCollections query,
            ExecutionContext ec) throws ActionException {
        BeanSession beanSession = TupeloStore.getInstance().getBeanSession();
        CollectionBeanUtil cbu = new CollectionBeanUtil(beanSession);
        PersonBeanUtil pbu = new PersonBeanUtil(beanSession);

        int limit = query.getLimit();
        int offset = query.getOffset();
        ArrayList<CollectionBean> collections = new ArrayList<CollectionBean>();
        List<Resource> seen = new LinkedList<Resource>();
        try {
            int dups = 1;
            while (dups > 0) {
                int news = 0;
                dups = 0;

                Unifier uf = createUnifier(query, limit, offset);

                Table<Resource> result = TupeloStore.getInstance().unifyExcludeDeleted(uf, "collection");

                for (Tuple<Resource> row : result) {
                    int r = 0;
                    Resource subject = row.get(r++);
                    Resource creator = row.get(r++);
                    Resource title = row.get(r++);
                    Resource description = row.get(r++);
                    Resource dateCreated = row.get(r++);
                    Resource dateModified = row.get(r++);
                    if (subject != null) {
                        try {
                            if (!seen.contains(subject)) { // FIXME: because of this logic, we may return fewer than the limit!
                                CollectionBean colBean = new CollectionBean();
                                // FIXME use Rob's BeanFactory instead of this hardcoded way
                                colBean.setUri(subject.getString());
                                if (creator != null) {
                                    colBean.setCreator(pbu.get(creator));
                                }
                                if (title != null) {
                                    colBean.setTitle(title.getString());
                                }
                                if (description != null) {
                                    colBean.setDescription(description.getString());
                                }
                                if (dateCreated != null) {
                                    colBean.setCreationDate((Date) ObjectResourceMapping.object(dateCreated));
                                }
                                if (dateModified != null) {
                                    colBean.setLastModifiedDate((Date) ObjectResourceMapping.object(dateModified));
                                }
                                collections.add(colBean);
                                seen.add(subject);
                                news++;
                            } else {
                                dups++;
                            }
                        } catch (OperatorException x) {
                            log.log(Level.SEVERE, "Unable to fetch collection " + subject, x);
                        }
                    }
                }
                if (limit > 0 && dups > 0) {
                    limit = dups;
                    offset += news; // FIXME: wow, this is a hack
                }
            }
        } catch (OperatorException e1) {
            log.log(Level.SEVERE, null, e1);
        }

        ListQueryResult<CollectionBean> result = new ListQueryResult<CollectionBean>(collections);
        result.setTotalCount(getCollectionCount());
        return result;
    }

    /**
     *
     * @param query
     * @param limit
     * @param offset
     * @return
     */
    private static Unifier createUnifier(ListQueryCollections query, int limit, int offset) {
        Unifier uf = new Unifier();
        uf.addPattern("collection", Rdf.TYPE,
                CollectionBeanUtil.COLLECTION_TYPE);
        Resource sortKey = DcTerms.DATE_CREATED;
        if (query.getSortKey() != null) {
            sortKey = Resource.uriRef(query.getSortKey());
        }
        uf.addPattern("collection", sortKey, "o", true);
        // now add columns to fetch the info we need to construct minimal CollectionBeans
        //
        uf.addPattern("collection", Dc.CREATOR, "creator", true);
        uf.addPattern("collection", Dc.TITLE, "title", true);
        uf.addPattern("collection", Dc.DESCRIPTION, "description", true);
        uf.addPattern("collection", DcTerms.DATE_CREATED, "dateCreated", true);
        uf.addPattern("collection", DcTerms.DATE_MODIFIED, "dateModified", true);
        uf.setColumnNames("collection", "creator", "title", "description", "dateCreated", "dateModified", "o");
        if (limit > 0) {
            uf.setLimit(limit);
        }
        if (offset > 0) {
            uf.setOffset(offset);
        }
        if (!query.isDesc()) {
            uf.addOrderBy("o");
        } else {
            uf.addOrderByDesc("o");
        }
        return uf;
    }
    private static Memoized<Integer> collectionCount;

    private static int getCollectionCount() {
        if (collectionCount == null) {
            collectionCount = new Memoized<Integer>() {

                @Override
                public Integer computeValue() {
                    Unifier u = new Unifier();
                    u.setColumnNames("c");
                    u.addPattern("c", Rdf.TYPE, MMDB.COLLECTION_TYPE);
                    try {
                        long then = System.currentTimeMillis();
                        Table<Resource> result = TupeloStore.getInstance().unifyExcludeDeleted(u, "c");
                        int count = 0;
                        for (Tuple<Resource> row : result) {
                            count++;
                        }
                        long ms = System.currentTimeMillis() - then;
                        log.log(Level.INFO, "counted {0} collection(s) in {1}ms", new Object[]{count, ms});
                        return count;
                    } catch (OperatorException e) {
                        log.log(Level.SEVERE, null, e);
                        return 0;
                    }
                }
            };
            collectionCount.setTtl(30000);
        }
        return collectionCount.getValue();
    }

    @Override
    public Class<ListQueryCollections> getActionType() {
        return ListQueryCollections.class;
    }

    @Override
    public void rollback(ListQueryCollections a, ListQueryResult<CollectionBean> r, ExecutionContext ec) throws ActionException {
    }
}
