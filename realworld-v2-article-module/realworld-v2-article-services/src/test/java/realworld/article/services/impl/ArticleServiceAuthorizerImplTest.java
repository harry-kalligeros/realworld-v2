package realworld.article.services.impl;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import java.util.UUID;
import java.util.function.Function;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.article.model.ArticleBase;
import realworld.article.model.ArticleCombinedFullData;
import realworld.article.model.ArticleCreationData;
import realworld.authorization.NotAuthenticatedException;
import realworld.authorization.service.Authorization;

/**
 * Tests for the {@link ArticleServiceAuthorizer}.
 */
@EnableAutoWeld
@ExtendWith(MockitoExtension.class)
public class ArticleServiceAuthorizerImplTest {

	private static final String SLUG_TO_FIND_COMBINED_DATA = "SLUG_TO_FIND_COMBINED_DATA";
	private static final ArticleCombinedFullData FROM_FIND_FULL_DATA_BY_SLUG = new ArticleCombinedFullData();
	private static final ArticleCreationData ARTICLE_CREATION_DATA = mock(ArticleCreationData.class);
	private static final ArticleBase FROM_CREATE = mock(ArticleBase.class);
	private static final String AUTHOR_ID = UUID.randomUUID().toString();

	static {
		when(ARTICLE_CREATION_DATA.getAuthorId()).thenReturn(AUTHOR_ID);
	}

	@Produces @Mock
	private Authorization authorization;

	@Inject
	private ArticleServiceAuthorizerImpl sut;

	@Test
	void testCreateForInvalidUser() {
		doThrow(NotAuthenticatedException.class).when(authorization).requireUserId(AUTHOR_ID);
		@SuppressWarnings("unchecked")
		Function<ArticleCreationData, ArticleBase> mockDelegate = mock(Function.class);
		expectNotAuthenticatedException(() -> sut.create(ARTICLE_CREATION_DATA, mockDelegate));
		verifyZeroInteractions(mockDelegate);
	}

	@Test
	void testCreateForValidUser() {
		@SuppressWarnings("unchecked")
		Function<ArticleCreationData, ArticleBase> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(ARTICLE_CREATION_DATA)).thenReturn(FROM_CREATE);
		ArticleBase result = sut.create(ARTICLE_CREATION_DATA, mockDelegate);
		assertSame(FROM_CREATE, result);
		verify(mockDelegate).apply(ARTICLE_CREATION_DATA);
	}

	@Test
	void testFindFullDataBySlug() {
		@SuppressWarnings("unchecked")
		Function<String, ArticleCombinedFullData> mockDelegate = mock(Function.class);
		when(mockDelegate.apply(SLUG_TO_FIND_COMBINED_DATA)).thenReturn(FROM_FIND_FULL_DATA_BY_SLUG);
		ArticleCombinedFullData result = sut.findFullDataBySlug(SLUG_TO_FIND_COMBINED_DATA, mockDelegate);
		assertSame(FROM_FIND_FULL_DATA_BY_SLUG, result);
		verifyZeroInteractions(authorization);
	}

	private void expectNotAuthenticatedException(Runnable f) {
		try {
			f.run();
			fail("expected NotAuthenticatedException");
		}
		catch( NotAuthenticatedException expected ) {
			// expected
		}
	}
}
