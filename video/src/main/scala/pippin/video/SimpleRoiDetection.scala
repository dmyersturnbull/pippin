package kokellab.lorien.roi

import java.awt.Dimension
import java.awt.image.BufferedImage
import java.util

import boofcv.abst.feature.detect.line.{DetectLineHoughFoot, DetectLineHoughPolar}
import boofcv.alg.filter.blur.BlurImageOps
import boofcv.factory.feature.detect.line.{ConfigHoughFoot, ConfigHoughPolar, FactoryDetectLineAlgs}
import boofcv.gui.ListDisplayPanel
import boofcv.gui.feature.ImageLinePanel
import boofcv.gui.image.ShowImages
import boofcv.io.image.{ConvertBufferedImage, UtilImageIO}
import boofcv.struct.image.{GrayS16, GrayS8, GrayU8, ImageGray}
import georegression.struct.line.LineParametric2D_F32

import collection.JavaConverters._

class SimpleRoiDetection[T <: ImageGray[T], D <: ImageGray[D]](imageType: Class[T], derivativeType: Class[D])(params: DetectionParams) {

	private val nExpectedLines = params.nRows + params.nColumns + 2
	private val houghFootConfig = new ConfigHoughFoot(params.localMaxRadius, params.minCounts, params.minDistanceFromOrigin, params.thresholdEdge, (nExpectedLines * 2 + 8).toInt)

	private val detector: DetectLineHoughFoot[T, D] = FactoryDetectLineAlgs.houghFoot(houghFootConfig, imageType, derivativeType)

	def apply(image: T): util.List[LineParametric2D_F32] = {
		detector.detect(image)
	}

}

case class DetectionParams(nRows: Int, nColumns: Int, localMaxRadius: Int = 1, minCounts: Int = 120, minDistanceFromOrigin: Int = 0, thresholdEdge: Float = 3.toFloat)

object SimpleRoiDetection {

	def detect8Bit(nRows: Int, nColumns: Int)(image: BufferedImage): (BufferedImage, util.List[LineParametric2D_F32]) = {
		var t: GrayU8 = ConvertBufferedImage.convertFromSingle(image, null, classOf[GrayU8])
		val blurred = t.createSameShape()
//		t = BlurImageOps.gaussian(t, null, 5, 2, null)
//		t = BlurImageOps.mean(t, blurred, 1, null)
		ConvertBufferedImage.convertTo(t, image)
		(image, new SimpleRoiDetection[GrayU8, GrayS16](classOf[GrayU8], classOf[GrayS16])(DetectionParams(nRows, nColumns)).apply(t))
	}

	def display(image: BufferedImage, lines: java.util.List[LineParametric2D_F32]): Unit = {
		val listPanel = new ListDisplayPanel()
		val gui = new ImageLinePanel()
		gui.setBackground(image)
		gui.setLines(lines)
		gui.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()))
		listPanel.addItem(gui, "Found Lines")
		ShowImages.showWindow(listPanel, "Detected Lines", true)
	}

	def main(args: Array[String]): Unit = {
		val image = UtilImageIO.loadImage("/home/dmyerstu/desktop/roi.jpg")
		require(image != null)
		val output = detect8Bit(12, 8)(image)
		display(output._1, output._2)
	}

}