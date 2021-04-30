package hu.hvj.marci.gifviewer;

public class GraphicBlock extends Data {

	private final GraphicControlExtension graphicControlExtension;
	private final GraphicRenderingBlock graphicRenderingBlock;

	public GraphicBlock(GraphicControlExtension gce, GraphicRenderingBlock grb) {
		graphicControlExtension = gce;
		graphicRenderingBlock = grb;
	}

	public GraphicControlExtension getGraphicControlExtension() {
		return graphicControlExtension;
	}

	public GraphicRenderingBlock getGraphicRenderingBlock() {
		return graphicRenderingBlock;
	}

}
