package de.mq.portfolio;

import java.util.UUID;

import org.junit.Test;
import org.springframework.data.annotation.Id;
import org.springframework.test.util.ReflectionTestUtils;

import org.junit.Assert;

public class IdentifierAwareTest {

	private static final String ID = UUID.randomUUID().toString();
	public final IdentifierAware<String> likeAVirgin = new IdentifierAware<String>() {
		@Id
		private String id;

	};

	@Test
	public final void emptyId() {
		Assert.assertNull(likeAVirgin.id());
	}

	@Test
	public final void id() {
		ReflectionTestUtils.setField(likeAVirgin, "id", ID);
		Assert.assertEquals(ID, likeAVirgin.id());
	}

	@Test(expected = IllegalStateException.class)
	public final void missingAnnotation() {
		final IdentifierAware<String> likeAVirgin = new IdentifierAware<String>() {
			@SuppressWarnings("unused")
			private String id;
		};

		likeAVirgin.id();
	}

}
