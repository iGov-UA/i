package org.igov.service.business.action.item;

import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.igov.model.action.item.Service;
import org.igov.model.action.item.ServiceTag;
import org.igov.model.action.item.ServiceTagLink;
import org.igov.model.action.item.ServiceTagRelation;
import org.igov.model.core.BaseEntityDao;
import org.igov.util.cache.CachedInvocationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

/**
 * User: goodg_000
 * Date: 10.07.2016
 * Time: 16:44
 */
@org.springframework.stereotype.Service
public class ServiceTagService {
    private static final long FAKE_ROOT_TAG_ID  = 0;
    private static final String GET_SERVICE_TAG_TREE_CACHE_KEY = "ServiceTagService.getServiceTagTree";
    private static final String GET_TAG_ID_TO_SERVICES_CACHE_KEY = "ServiceTagService.getTagIdToServicesMap";

    @Autowired
    private BaseEntityDao<Long> baseEntityDao;

    @Autowired
    private CachedInvocationBean cachedInvocationBean;

    public List<ServiceTagTreeNodeVO> getCatalogTreeTag(Long nID_Category, String sFind,
                                                        List<String> asID_Place_UA, boolean bShowEmptyFolders,
                                                        boolean includeServices,
                                                        Long nID_ServiceTag, Boolean bRoot) {
        List<ServiceTagTreeNodeVO> res = new ArrayList<>();

        ServiceTagTree tree = getServiceTagTreeCached();
        Map<Long, List<Service>> tagIdToServices = getTagIdToServicesMapCached();

        for (ServiceTagTreeNode rootTagNode : tree.getRootTagNodes()) {
            final ServiceTag parentTag = rootTagNode.getTag();
            if (isSuitable(tagIdToServices.get(parentTag.getId()), nID_Category, sFind, asID_Place_UA)) {
                ServiceTagTreeNodeVO nodeVO = new ServiceTagTreeNodeVO();
                nodeVO.setoServiceTag_Root(parentTag);
                for (ServiceTagTreeNode childNode : rootTagNode.getChildren()) {
                    final ServiceTag childTag = childNode.getTag();
                    if (isSuitable(tagIdToServices.get(childTag.getId()), nID_Category, sFind, asID_Place_UA)) {
                        nodeVO.addChild(childTag);
                    }
                }

                if (!nodeVO.getaServiceTag_Child().isEmpty() || bShowEmptyFolders) {
                    res.add(nodeVO);
                }
            }
        }

        return res;
    }

    private boolean isSuitable(List<Service> services,
                               Long nID_Category, String sFind, List<String> asID_Place_UA) {
        if (CollectionUtils.isEmpty(services)) {
            return false;
        }

        boolean res = false;
        for (Service service : services) {
            if (isSuitable(service, nID_Category, sFind, asID_Place_UA)) {
                res = true;
                break;
            }
        }

        return res;
    }

    private boolean isSuitable(Service service,
                               Long nID_Category, String sFind, List<String> asID_Place_UA) {
        boolean res = true;
        res = nID_Category.equals(service.getSubcategory().getCategory().getId());
        if (res && sFind != null) {
            res = service.getName().toLowerCase().contains(sFind.toLowerCase());
        }
        if (res && CollectionUtils.isNotEmpty(asID_Place_UA)) {
            // TODO implement check
        }
        return res;
    }

    private ServiceTagTree getServiceTagTreeCached() {
        return cachedInvocationBean.invokeUsingCache(new CachedInvocationBean.Callback<ServiceTagTree>(
                GET_SERVICE_TAG_TREE_CACHE_KEY) {
            @Override
            public ServiceTagTree execute() {
                return getServiceTagTree();
            }
        });
    }

    private ServiceTagTree getServiceTagTree() {
        List<ServiceTagRelation> relations = new ArrayList<>(baseEntityDao.findAll(ServiceTagRelation.class));
        Map<ServiceTag, ServiceTagTreeNode> tagToNodeMap = new HashMap<>();

        Set<ServiceTag> parentTags = new LinkedHashSet<>();
        Set<ServiceTag> childTags = new HashSet<>();

        for (ServiceTagRelation relation : relations) {
            final ServiceTag parent = relation.getServiceTag_Parent();
            final ServiceTag child = relation.getServiceTag_Child();

            ServiceTagTreeNode parentNode = null;
            if (parent.getId() != FAKE_ROOT_TAG_ID) {
                parentNode = tagToNodeMap.get(parent);
                if (parentNode == null) {
                    parentTags.add(parent);
                    parentNode = new ServiceTagTreeNode(parent);
                    tagToNodeMap.put(parent, parentNode);
                }
            }

            ServiceTagTreeNode childNode = tagToNodeMap.get(child);
            if (childNode == null) {
                childTags.add(child);
                childNode = new ServiceTagTreeNode(child);
                tagToNodeMap.put(child, childNode);
            }

            if (parentNode != null) {
                parentNode.addChild(childNode);
            }
        }

        Set<ServiceTag> rootTags = new LinkedHashSet<>(parentTags);
        rootTags.removeAll(childTags);

        final List<ServiceTagTreeNode> rootTagNodes = rootTags.stream().map(tagToNodeMap::get).collect(
                Collectors.toList());
        return new ServiceTagTree(rootTagNodes);
    }

    private Map<Long, List<Service>> getTagIdToServicesMapCached() {
        return cachedInvocationBean.invokeUsingCache(new CachedInvocationBean.Callback<Map<Long, List<Service>>>(
                GET_TAG_ID_TO_SERVICES_CACHE_KEY) {
            @Override
            public Map<Long, List<Service>> execute() {
                return getTagIdToServicesMap();
            }
        });
    }

    private Map<Long, List<Service>> getTagIdToServicesMap() {
        Map<Long, List<Service>> res = new HashMap<>();

        List<ServiceTagLink> links = new ArrayList<>(baseEntityDao.findAll(ServiceTagLink.class));
        for (ServiceTagLink link : links) {
            final Long serviceTagId = link.getServiceTag().getId();
            List<Service> services = res.get(serviceTagId);
            if (services == null) {
                services = new ArrayList<>();
                res.put(serviceTagId, services);
            }
            services.add(link.getService());
        }

        return res;
    }
}
