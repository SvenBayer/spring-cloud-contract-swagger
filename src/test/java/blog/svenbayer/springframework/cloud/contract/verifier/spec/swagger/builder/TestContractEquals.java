package blog.svenbayer.springframework.cloud.contract.verifier.spec.swagger.builder;

import org.json.JSONException;
import org.junit.jupiter.api.function.Executable;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.cloud.contract.spec.Contract;
import org.springframework.cloud.contract.spec.internal.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sven Bayer
 */
public class TestContractEquals {

	private static final String LINE_SEPS = "\\r\\n|\\n|\\r";

	public static void assertContractEquals(Collection<Contract> expected, Collection<Contract> actual) {
		final String msg = "Contract List:\n";
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}
		assertEquals(expected.size(), actual.size());

		Iterator<Contract> expectedIterator = expected.iterator();
		Iterator<Contract> actualIterator = actual.iterator();

		List<Executable> contractExecutables = new ArrayList<>();
		AtomicInteger index = new AtomicInteger(0);
		while (expectedIterator.hasNext()) {
			final Contract expectedNext = expectedIterator.next();
			final Contract actualNext = actualIterator.next();
			final String contractMsg = msg + "Contract List Index: " + index.getAndIncrement() + "\n";
			contractExecutables.add(() -> assertContractEquals(expectedNext, actualNext, contractMsg));
		}

		assertAll("contract list", contractExecutables.stream());
	}

	private static void assertContractEquals(Contract expected, Contract actual, String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}

		assertEquals(expected.getName(), actual.getName());
		msg = msg + "Contract: " + expected.getName() + "\n";

		assertEquals(expected.getPriority(), actual.getPriority(), msg);
		assertEquals(expected.getLabel(), actual.getLabel(), msg);
		assertEquals(expected.getDescription(), actual.getDescription(), msg);
		assertEquals(expected.isIgnored(), actual.isIgnored(), msg);
		assertEquals(expected.getInput(), actual.getInput(), msg);
		assertEquals(expected.getOutputMessage(), actual.getOutputMessage(), msg);

		assertRequestEquals(expected, actual, msg);

		assertResponseEquals(expected, actual, msg);
	}

	private static void assertResponseEquals(Contract expected, Contract actual, String msg) {
		msg = msg + "Response:\n";
		Response actualResponse = actual.getResponse();
		Response expectedResponse = expected.getResponse();
		if (assertBothOrNonNull(expectedResponse, actualResponse, msg)) {
			return;
		}

		assertEquals(expectedResponse.getProperty(), actualResponse.getProperty(), msg);
		assertEquals(expectedResponse.getHttpStatus(), actualResponse.getHttpStatus(), msg);
		assertDslPropertyEquals(expectedResponse.getStatus(), actualResponse.getStatus(), msg);
		assertDslPropertyEquals(expectedResponse.getDelay(), actualResponse.getDelay(), msg);
		assertHeadersEquals(expectedResponse.getHeaders(), actualResponse.getHeaders(), msg);
		assertEquals(expectedResponse.getCookies(), actualResponse.getCookies(), msg);
		assertDslPropertyEquals(expectedResponse.getBody(), actualResponse.getBody(), msg);
		assertEquals(expectedResponse.isAsync(), actualResponse.isAsync(), msg);
		assertEquals(expectedResponse.getBodyMatchers(), actualResponse.getBodyMatchers(), msg);
	}

	private static void assertRequestEquals(Contract expected, Contract actual, String msg) {
		msg = msg + "Request:\n";
		Request actualRequest = actual.getRequest();
		Request expectedRequest = expected.getRequest();
		if (assertBothOrNonNull(expectedRequest, actualRequest, msg)) {
			return;
		}

		assertEquals(expectedRequest.getProperty(), actualRequest.getProperty(), msg);
		assertEquals(expectedRequest.getHttpMethods(), actualRequest.getHttpMethods(), msg);
		assertDslPropertyEquals(expectedRequest.getMethod(), actualRequest.getMethod(), msg);
		assertEquals(expectedRequest.getUrl(), actualRequest.getUrl(), msg);
		assertUrlPathEquals(expectedRequest.getUrlPath(), actualRequest.getUrlPath(), msg);
		assertHeadersEquals(expectedRequest.getHeaders(), actualRequest.getHeaders(), msg);
		assertEquals(expectedRequest.getCookies(), actualRequest.getCookies(), msg);
		assertDslPropertyEquals(expectedRequest.getBody(), actualRequest.getBody(), msg);
		assertEquals(expectedRequest.getMultipart(), actualRequest.getMultipart(), msg);
		assertEquals(expectedRequest.getBodyMatchers(), actualRequest.getBodyMatchers(), msg);
	}

	private static void assertUrlPathEquals(UrlPath expected, UrlPath actual, String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}

		assertAll("urlpath",
				() -> assertPossiblePatternEquals(expected.getClientValue(), actual.getClientValue(), msg + "ClientValue:\n"),
				() -> assertPossiblePatternEquals(expected.getServerValue(), actual.getServerValue(), msg + "ServerValue:\n")
		);

		assertQueryParametersEquals(expected, actual, msg);
	}

	private static void assertQueryParametersEquals(UrlPath expected, UrlPath actual, String msg) {
		if (assertBothOrNonNull(expected.getQueryParameters(), actual.getQueryParameters(), msg)) {
			return;
		}
		List<QueryParameter> expectedParameters = expected.getQueryParameters().getParameters();
		List<QueryParameter> actualParameters = actual.getQueryParameters().getParameters();
		if (assertBothOrNonNull(expectedParameters, actualParameters, msg)) {
			return;
		}

		assertEquals(expectedParameters.size(), actualParameters.size(), msg);

		List<Executable> queryParameterExecutables = new ArrayList<>();
		final String baseMsg = msg;
		for (int i = 0; i < expectedParameters.size(); i++) {
			QueryParameter expectedParameter = expectedParameters.get(i);
			QueryParameter actualParameter = actualParameters.get(i);

			queryParameterExecutables.add(() -> {
				assertEquals(expectedParameter.getName(), actualParameter.getName(), msg);
				final String queryMsg = baseMsg + "QueryParameter: " + expectedParameter.getName() + "\n";

				assertAll("query parameter",
						() -> assertPossiblePatternEquals(expectedParameter.getClientValue(), actualParameter.getClientValue(), queryMsg + "ClientValue:\n"),
						() -> assertPossiblePatternEquals(expectedParameter.getServerValue(), actualParameter.getServerValue(), queryMsg + "ServerValue:\n")
				);
				assertEquals(expectedParameter.isSingleValue(), actualParameter.isSingleValue(), msg);
			});
		}
		assertAll("query parameters", queryParameterExecutables.stream());
	}

	static void assertDslPropertyEquals(DslProperty expected, DslProperty actual, String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}

		assertAll("dsl property",
				() -> assertPossiblePatternEquals(expected.getClientValue(), actual.getClientValue(), msg + "ClientValue:\n"),
				() -> assertPossiblePatternEquals(expected.getServerValue(), actual.getServerValue(), msg + "ServerValue:\n")
		);
		assertEquals(expected.isSingleValue(), actual.isSingleValue(), msg);
	}

	private static void assertHeadersEquals(Headers expected, Headers actual, String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}
		assertHeaderEquals(expected.getEntries(), actual.getEntries(), msg);
		assertEquals(expected.getHttpHeaders(), actual.getHttpHeaders(), msg);
		assertEquals(expected.getMediaTypes(), actual.getMediaTypes(), msg);
		assertEquals(expected.getMessagingHeaders(), actual.getMessagingHeaders(), msg);
	}

	private static void assertHeaderEquals(Set<Header> expected, Set<Header> actual, String msg) {
		if (assertBothOrNonNull(expected, actual, msg)) {
			return;
		}
		Iterator<Header> expectedIterator = expected.iterator();
		Iterator<Header> actualIterator = actual.iterator();

		List<Executable> headerExecutables = new ArrayList<>();
		while (expectedIterator.hasNext()) {
			Header expectedHeader = expectedIterator.next();
			Header actualHeader = actualIterator.next();

			headerExecutables.add(() -> {
				assertEquals(expectedHeader.getName(), actualHeader.getName(), msg);
				final String headerMsg = msg + "Header: " + expectedHeader.getName() + "\n";
				assertAll("header",
						() -> assertPossiblePatternEquals(expectedHeader.getClientValue(), actualHeader.getClientValue(), headerMsg + "ClientValue:\n"),
						() -> assertPossiblePatternEquals(expectedHeader.getServerValue(), actualHeader.getServerValue(), headerMsg + "ServerValue:\n")
				);
			});
		}
		assertAll("header", headerExecutables.stream());
	}

	private static void assertPossiblePatternEquals(Object expected, Object actual, String msg)
	{
		if (expected instanceof Pattern) {
			msg = msg + "Pattern:\n";
			assertEquals(Pattern.class, actual.getClass(), msg);
			Pattern expectedPattern = (Pattern) expected;
			Pattern actualPattern = (Pattern) actual;
			assertEquals(expectedPattern.pattern(), actualPattern.pattern(), msg);
		} else if (expected instanceof DslProperty) {
			msg = msg + "DslProperty:\n";
			DslProperty expectedDslProperty = (DslProperty) expected;
			DslProperty actualDslProperty = (DslProperty) actual;
			assertDslPropertyEquals(expectedDslProperty, actualDslProperty, msg);
		} else {
			assertEqualsNoLineSeparator(expected, actual, msg);
		}
	}

	private static boolean assertBothOrNonNull(Object expected, Object actual, String msg) {
		if (expected == null) {
			assertNull(actual, msg);
			return true;
		} else {
			assertNotNull(actual, msg);
			return false;
		}
	}

	private static void assertEqualsNoLineSeparator(Object expected, Object actual, String msg)
	{
		if (expected == null) {
			assertNull(actual, msg);
		} else {
			assertNotNull(actual, msg);
			String cleanedUpExpected = expected.toString().replaceAll(LINE_SEPS, System.lineSeparator());
			String cleanedUpActual = actual.toString().replaceAll(LINE_SEPS, System.lineSeparator());
			try
			{
				JSONAssert.assertEquals(cleanedUpExpected, cleanedUpActual, false);
			}
			catch (JSONException ex)
			{
				assertEquals(cleanedUpExpected, cleanedUpActual, msg);
			}
		}
	}
}
