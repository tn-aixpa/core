package it.smartcommunitylabdhub.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.template.Template;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.TemplateFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableTemplateService;
import jakarta.validation.constraints.NotNull;

@Service
public class TemplateServiceImpl implements SearchableTemplateService {
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	ResourcePatternResolver resourceResolver; 
	
	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
	
	LoadingCache<String, List<Template>> templateCache = CacheBuilder.newBuilder()
			.expireAfterWrite(60, TimeUnit.MINUTES)
			.build(new CacheLoader<String, List<Template>>() {
				@Override
				public List<Template> load(String key) throws Exception {
					List<Template> result = new ArrayList<>();
					Resource[] resources = resourceResolver.getResources("classpath:templates/" + key + "/*.yml");
					for(Resource resource : resources) {
						Template template = mapper.readValue(resource.getFile(), Template.class);
						result.add(template);
					}
					return result;
				}
			});
	
	private List<Template> filterTemplate(Pageable pageable, String type, TemplateFilter filter) throws Exception {
		List<Template> list = templateCache.get(type);
		return list.stream().filter(f -> {
			boolean isOk = true;
			if(StringUtils.hasLength(filter.getName())) {
				if(StringUtils.hasLength(f.getName())) {
					isOk &= f.getName().toLowerCase().contains(filter.getName().toLowerCase());
				} else {
					isOk &= false;
				}
			}
			if(StringUtils.hasLength(filter.getKind())) {
				if(StringUtils.hasLength(f.getKind())) {
					isOk &= f.getKind().toLowerCase().equals(filter.getKind().toLowerCase());
				} else {
					isOk &= false;
				}
			}
			return isOk;
		}).collect(Collectors.toList());
	}
	
	private Template filterTemplate(String type, String id) throws Exception {
		List<Template> list = templateCache.get(type);
		return list.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public Page<Template> searchTemplates(Pageable pageable, @NotNull String type, TemplateFilter filter)
			throws SystemException {
		 try {
			 List<Template> list = filterTemplate(pageable, type, filter);
			 int start = (int) pageable.getOffset();
			 int end = Math.min((start + pageable.getPageSize()), list.size());
			 List<Template> pageContent  = list.subList(start, end);
			 return new PageImpl<>(pageContent, pageable, list.size());
		} catch (Exception e) {
			throw new SystemException("error retriving templates:" + e.getMessage(), e);
		}	
	}

	@Override
	public Template getTemplate(@NotNull String type, @NotNull String id) throws SystemException {
		 try {
			 return filterTemplate(type, id);
		} catch (Exception e) {
			throw new SystemException("error retriving templates:" + e.getMessage(), e);
		}	
	}

}
