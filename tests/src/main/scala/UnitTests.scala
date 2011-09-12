package polaris.games.multiplication.tests

import junit.framework.Assert._
import _root_.android.test.AndroidTestCase

class UnitTests extends AndroidTestCase {
  def testPackageIsCorrect {
    assertEquals("polaris.games.multiplication", getContext.getPackageName)
  }
}