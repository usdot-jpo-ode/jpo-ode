package us.dot.its.jpo.ode.importer.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.dot.its.jpo.ode.model.OdeSpatMetadata.SpatSource;

public class SpatLogFileParser extends LogFileParser {
	private static final Logger logger = LoggerFactory.getLogger(SpatLogFileParser.class.getName());
	private static final int RX_FROM_LENGTH = 1;

	private SpatSource spatSource;

	public SpatLogFileParser() {
		super();
		setIntersectionParser(new IntersectionParser());
		setTimeParser(new TimeParser());
		setSecResCodeParser(new SecurityResultCodeParser());
		setPayloadParser(new PayloadParser());
	}
	
	@Override
	public ParserStatus parseFile(BufferedInputStream bis, String fileName) throws FileParserException {
		ParserStatus status;
		try {
			status = super.parseFile(bis, fileName);
			if (status != ParserStatus.COMPLETE)
				return status;

			if (getStep() == 1) {
				status = parseStep(bis, RX_FROM_LENGTH);
				if (status != ParserStatus.COMPLETE)
					return status;
				setSpatSource(readBuffer);
			}

			if (getStep() == 2) {
				status = nextStep(bis, fileName, intersectionParser);
				if (status != ParserStatus.COMPLETE)
					return status;
			}

			if (getStep() == 3) {
				status = nextStep(bis, fileName, timeParser);
				if (status != ParserStatus.COMPLETE)
					return status;
			}

			if (getStep() == 4) {
				status = nextStep(bis, fileName, secResCodeParser);
				if (status != ParserStatus.COMPLETE)
					return status;
			}

			if (getStep() == 5) {
				status = nextStep(bis, fileName, payloadParser);
				if (status != ParserStatus.COMPLETE)
					return status;
			}

			resetStep();
			status = ParserStatus.COMPLETE;

		} catch (Exception e) {
			throw new FileParserException("Error parsing " + fileName, e);
		}

		return status;
	}

	
	public SpatSource getSpatSource() {
		return spatSource;
	}

	public void setSpatSource(SpatSource spatSource) {
		this.spatSource = spatSource;
	}

	public void setSpatSource(byte[] code) {
		try {
			setSpatSource(SpatSource.values()[code[0]]);
		} catch (Exception e) {
			logger.error("Invalid SpatSource: {}. Valid values are {}-{} inclusive", code, 0, SpatSource.values());
			setSpatSource(SpatSource.unknown);
		}
	}

	@Override
	public void writeTo(OutputStream os) throws IOException {
		os.write((byte)spatSource.ordinal());
		super.writeTo(os);
	}
}
