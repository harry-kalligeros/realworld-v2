package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.SimpleValidationException;
import realworld.article.dao.ArticleDao;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCreationData;
import realworld.authentication.AuthenticationContext;
import realworld.services.DateTimeService;

/**
 * Tests for the {@link ArticleServiceImpl}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class ArticleServiceImplTest {

	private static final String ARTICLE_ID = UUID.randomUUID().toString();
	private static final String AUTHOR_ID = UUID.randomUUID().toString();
	private static final LocalDateTime CREATED_AT = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
	private static final LocalDateTime UPDATED_AT = LocalDateTime.now();
	private static final String DESCRIPTION = "Description";
	private static final String SLUG = "slug";
	private static final String TITLE = "Title";
	private static final String BODY = "Body";

	@Produces @Mock
	private ArticleDao articleDao;

	@Produces @Mock @Slugifier
	private Function<String,String> slugifier;

	@Produces @Mock(lenient = true)
	private DateTimeService dateTimeService;

	@Produces @Mock
	private AuthenticationContext authenticationContext;

	@Inject
	private ArticleServiceImpl sut;

	@Test
	void testCreateWithDuplicateSlug() {
		ArticleCreationData creationData = prepareForCreation();
		when(articleDao.slugExists(SLUG)).thenReturn(true);
		try {
			sut.create(creationData);
			fail("should fail on duplicate slug");
		}
		catch( SimpleValidationException expected ) {
			// expected
		}
	}

	@Test
	void testCreate() {
		ArticleCreationData creationData = prepareForCreation();
		when(articleDao.slugExists(SLUG)).thenReturn(false);
		when(articleDao.create(any(ArticleCreationData.class), anyString(), any(LocalDateTime.class))).thenReturn(ARTICLE_ID);
		ArticleBase result = sut.create(creationData);
		assertNotNull(result);
		assertEquals(ARTICLE_ID, result.getId());
		assertEquals(TITLE, result.getTitle());
		assertEquals(SLUG, result.getSlug());
		assertEquals(DESCRIPTION, result.getDescription());
		assertEquals(CREATED_AT, result.getCreatedAt());
		assertNull(result.getUpdatedAt());
		verify(articleDao).create(eq(creationData), eq(SLUG), eq(CREATED_AT));
	}

	private ArticleCreationData prepareForCreation() {
		when(dateTimeService.getNow()).thenReturn(CREATED_AT);
		when(slugifier.apply(TITLE)).thenReturn(SLUG);
		ArticleCreationData creationData = mock(ArticleCreationData.class, withSettings().lenient());
		when(creationData.getAuthorId()).thenReturn(AUTHOR_ID);
		when(creationData.getBody()).thenReturn(BODY);
		when(creationData.getDescription()).thenReturn(DESCRIPTION);
//		when(creationData.getTagList())
		when(creationData.getTitle()).thenReturn(TITLE);
		return creationData;
	}
}
