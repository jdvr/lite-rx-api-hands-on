/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.literx;

import java.time.Duration;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.pivotal.literx.domain.User;
import static org.junit.Assert.fail;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Learn how to use StepVerifier to test Mono, Flux or any other kind of Reactive Streams Publisher.
 *
 * @author Sebastien Deleuze
 * @see <a href="http://next.projectreactor.io/ext/docs/api/reactor/test/StepVerifier.html">StepVerifier Javadoc</a>
 */
public class Part03StepVerifier {

//========================================================================================

	@Test
	public void expectElementsThenComplete() {
		expectFooBarComplete(Flux.just("foo", "bar"));
	}

	// TODO Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then completes successfully.
	void expectFooBarComplete(Flux<String> flux) {
		StepVerifier.create(flux)
				.expectNext("foo", "bar")
				.expectComplete()
				.verify();
	}

//========================================================================================

	@Test
	public void expect2ElementsThenError() {
		expectFooBarError(Flux.just("foo", "bar").concatWith(Mono.error(new RuntimeException())));
	}

	// TODO Use StepVerifier to check that the flux parameter emits "foo" and "bar" elements then a RuntimeException error.
	void expectFooBarError(Flux<String> flux) {
		StepVerifier.create(flux)
				.expectNext("foo", "bar")
				.expectError(RuntimeException.class)
				.verify();
	}

//========================================================================================

	@Test
	public void expectElementsWithThenComplete() {
		expectSkylerJesseComplete(Flux.just(new User("swhite", null, null), new User("jpinkman", null, null)));
	}

	// TODO Use StepVerifier to check that the flux parameter emits a User with "swhite" username and another one with "jpinkman" then completes successfully.
	void expectSkylerJesseComplete(Flux<User> flux) {
		Function<String, Predicate<User>> userNameIs = s -> u -> u.getUsername().equals(s);
		StepVerifier.create(flux)
				.expectNextMatches(userNameIs.apply("swhite"))
				.expectNextMatches(userNameIs.apply("jpinkman"))
				.expectComplete()
				.verify();
	}

//========================================================================================

	@Test
	public void count() {
		expect10Elements(Flux.interval(Duration.ofSeconds(1)).take(10));
	}

	// TODO Expect 10 elements then complete and notice how long it takes for running the test
	void expect10Elements(Flux<Long> flux) {
		StepVerifier.create(flux)
				.thenAwait(Duration.ofSeconds(10))
				.expectNextCount(10)
				.expectComplete()
				.verify();
	}

//========================================================================================

	@Test
	public void countWithVirtualTime() {
		expect3600Elements(() -> Flux.interval(Duration.ofSeconds(1)).take(3600));
	}

	// TODO Expect 3600 elements then complete using the virtual time capabilities provided via StepVerifier.withVirtualTime() and notice how long it takes for running the test
	void expect3600Elements(Supplier<Flux<Long>> supplier) {

		StepVerifier.withVirtualTime(supplier)
				.thenAwait(Duration.ofSeconds(3600))
				.expectNextCount(3600)
				.expectComplete()
				.verify();
	}

}
