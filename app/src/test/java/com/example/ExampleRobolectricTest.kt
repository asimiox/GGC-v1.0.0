package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GGC M.B.Din", appName)
  }

  @Test
  fun `verify official faculty registry has valid records`() {
    val facultyList = com.example.data.datasource.OfficialFacultyData.facultyList
    assertEquals(41, facultyList.size)
    val principal = facultyList.first()
    assertEquals("Amir Ahmed", principal.name)
    assertEquals("Principal", principal.designation)
  }
}
