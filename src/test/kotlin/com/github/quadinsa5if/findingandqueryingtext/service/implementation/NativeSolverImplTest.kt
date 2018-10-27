package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.MutableVocabulary
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation.InMemoryVocabularyImpl
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object NativeSolverImplTest : Spek({

  val d1 = 1
  val d2 = 2
  val d3 = 3
  val d4 = 4
  val d5 = 5
  val d6 = 6

  val postingListT1 = listOf(
      Entry(d2, .9),
      Entry(d5, .8),
      Entry(d6, .7),
      Entry(d4, .6),
      Entry(d1, .5),
      Entry(d3, .4)
  )

  val postingListT2 = listOf(
      Entry(d3, .85),
      Entry(d5, .8),
      Entry(d2, .75),
      Entry(d6, .74),
      Entry(d1, .74),
      Entry(d4, .7)
  )

  val postingLists: MutableVocabulary = InMemoryVocabularyImpl()
  postingListT1.forEach { postingLists.putEntry("t1", it) }
  postingListT2.forEach { postingLists.putEntry("t2", it) }


  given("a native solver") {
    val solver: QuerySolver = NativeSolverImpl(postingLists)
    on("answering with parameters: terms=[\"t1\", \"t2\"], k=3") {
      val answer = solver.answer(arrayOf("t1", "t2"), 3)
      val first = answer.next();
      it("first result should be some") {
        assertThat("first result is some", first.isPresent, equalTo(true))
      }
      it("first result should be d2") {
        assertThat(d2, equalTo(first.get()))
      }
      val second = answer.next()
      it("second result should be some") {
        assertThat("second result is some", second.isPresent, equalTo(true))
      }
      it("second result should be d5") {
        assertThat(d5, equalTo(second.get()))
      }
      val third = answer.next()
      it("third result should be some") {
        assertThat("third result is some", third.isPresent, equalTo(true))
      }
      it("third result should be d6") {
        assertThat(d6, equalTo(third.get()))
      }
      val fourth = answer.next()
      it("fourth result should be none (empty)") {
        assertThat("fourth result is none (empty)", fourth.isPresent, equalTo(false))
      }
    }

    on("answering with parameters: terms[\"t1\"], k=2") {
      val answer = solver.answer(arrayOf("t1"), 2)
      val first = answer.next();
      it("first result should be some") {
        assertThat("first result is some", first.isPresent, equalTo(true))
      }
      it("first result should be d2") {
        assertThat(d2, equalTo(first.get()))
      }
      val second = answer.next()
      it("second result should be some") {
        assertThat("second result is some", second.isPresent, equalTo(true))
      }
      it("second result should be d5") {
        assertThat(d5, equalTo(second.get()))
      }
      val third = answer.next()
      it("third result should be none (empty)") {
        assertThat("third result is none (empty)", third.isPresent, equalTo(false))
      }
    }

  }

})