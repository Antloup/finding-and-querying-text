package com.github.quadinsa5if.findingandqueryingtext.service.implementation

import com.github.quadinsa5if.findingandqueryingtext.model.Entry
import org.jetbrains.spek.api.Spek
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object FaginSolvertImplTest : Spek({

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

})