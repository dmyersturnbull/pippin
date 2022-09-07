package kokellab.lorien.roi

import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage
import java.util

import boofcv.abst.feature.detect.line.{DetectLineHoughFoot, DetectLineHoughPolar}
import boofcv.abst.fiducial.calib.ConfigSquareGridBinary
import boofcv.alg.geo.calibration.CalibrationObservation
import boofcv.factory.feature.detect.line.{ConfigHoughFoot, ConfigHoughPolar, FactoryDetectLineAlgs}
import boofcv.factory.fiducial.FactoryFiducialCalibration
import boofcv.gui.ListDisplayPanel
import boofcv.gui.feature.{ImageLinePanel, VisualizeFeatures}
import boofcv.gui.image.ShowImages
import boofcv.io.image.{ConvertBufferedImage, UtilImageIO}
import boofcv.struct.image._
import georegression.struct.line.LineParametric2D_F32

import collection.JavaConverters._

class SmarterRoiDetection(params: SmarterDetectionParams) {

	private val detector = FactoryFiducialCalibration.binaryGrid(new ConfigSquareGridBinary(params.nRows, params.nColumns, 75, 25))

	def apply(image: GrayF32): CalibrationObservation = {
		detector.process(image)
		detector.getDetectedPoints
	}

}

case class SmarterDetectionParams(nRows: Int, nColumns: Int)

object SmarterRoiDetection {

	def main(args: Array[String]): Unit = {
		val image = UtilImageIO.loadImage("/home/dmyerstu/desktop/roi.jpg")
		require(image != null)
		val gray: GrayF32 = ConvertBufferedImage.convertFromSingle(image, null, classOf[GrayF32])
		val detector = new SmarterRoiDetection(SmarterDetectionParams(8, 12))
		val obs = detector.apply(gray)
		val g2 = image.createGraphics()
		println(obs.points.size())
		for (point <- obs.points.asScala) {
			VisualizeFeatures.drawPoint(g2, point.x, point.y, 3, Color.RED, true)
		}

		ShowImages.showWindow(image, "Calibration Points", true)
	}

}