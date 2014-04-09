package org.opencb.opencga.storage.alignment;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.*;
import org.opencb.commons.bioformats.alignment.Alignment;
import org.opencb.commons.bioformats.alignment.io.readers.AlignmentRegionDataReader;
import org.opencb.commons.bioformats.alignment.sam.io.AlignmentBamDataReader;
import org.opencb.commons.bioformats.alignment.sam.io.AlignmentSamDataReader;
import org.opencb.commons.test.GenericTest;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: josemi
 * Date: 3/13/14
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentProtoHelperTest extends GenericTest {
    @Ignore
    @Test
    public void protoAndUnproto1 () {
        String shortSam = getClass().getResource("/small.sam").getFile();
        AlignmentSamDataReader alignmentSamDataReader = new AlignmentSamDataReader(shortSam);
        alignmentSamDataReader.open();
        alignmentSamDataReader.pre();
        Alignment alignment1;
        Alignment alignment2;

        AlignmentRegionSummary summary;

        for (int i = 0; i < 900; i++) {
            alignment1 = alignmentSamDataReader.read();
            System.out.println("Leemos alignment " + i);
            if (alignment1 != null) {
                System.out.println("Creamos el summary");
                summary = new AlignmentRegionSummary(2);
                System.out.println("Lo llenamos");
                summary.addAlignment(alignment1);
                System.out.println("Cerramos el summary");
                summary.close();
                System.out.println(summary.getDefaultLen());

                alignment2 = AlignmentProtoHelper.toAlignment(AlignmentProtoHelper.toProto(alignment1, alignment1.getStart()/256*256, summary), summary, alignment1.getChromosome(), alignment1.getStart()/256*256);

                if(!printEquals(alignment1, alignment2)) {
                    System.out.println("failed alignment nº: " + i);
                }
            } else {
                System.out.println("the read gave null, ending reading...");
                break;
            }
        }

        alignmentSamDataReader.post();
        alignmentSamDataReader.close();
        System.out.println("protoAndUnproto finished!");
    }


/*
    @Test
    public void protoAndUnproto2 () {

//        String bam20 = getClass().getResource("/chrom20.bam").getFile();
//        AlignmentBamDataReader alignmentBamDataReader = new AlignmentBamDataReader(bam20);
//        alignmentBamDataReader.open();
//        alignmentBamDataReader.pre();
        String shortSam = getClass().getResource("/small.sam").getFile();
        AlignmentSamDataReader alignmentSamDataReader = new AlignmentSamDataReader(shortSam);
        alignmentSamDataReader.open();
        alignmentSamDataReader.pre();
        Alignment alignment1;
        Alignment alignment2;

        for (int i = 0; i < 100000; i++) {
//            alignment1 = alignmentBamDataReader.read();
            alignment1 = alignmentSamDataReader.read();
            if (alignment1 != null) {
                alignment2 = AlignmentProtoHelper.toAlignment(AlignmentProtoHelper.toProto(alignment1, alignment1.getStart()/256*256), alignment1.getChromosome(), alignment1.getStart()/256*256);

                if(!printEquals(alignment1, alignment2)) {
                    System.out.println("failed alignment nº: " + i);
                    fail();
                }
            } else {
                System.out.println("the read gave null, ending reading...");
                fail();
//                break;
            }
        }

        alignmentSamDataReader.post();
        alignmentSamDataReader.close();
//        alignmentBamDataReader.post();
//        alignmentBamDataReader.close();
        System.out.println("protoAndUnproto finished!");
    }
*/
    public boolean printEquals (Alignment alignment1, Alignment alignment2){
        boolean areEqual = true;

        if (!alignment1.equals(alignment2)){
            if (!alignment1.getName().equals(alignment2.getName())) {
                areEqual = false;
                System.out.println("name is not equal");
            }

            if (!alignment1.getChromosome().equals(alignment2.getChromosome())) {
                areEqual = false;
                System.out.println("chromosome is not equal");
            }
            if (!(alignment1.getStart() == alignment2.getStart())) {
                areEqual = false;
                System.out.println("Start is not equal: " + alignment1.getStart() + ", " + alignment2.getStart());
            }
            if (!(alignment1.getEnd() == alignment2.getEnd())) {
                areEqual = false;
                System.out.println("End is not equal");
            }
            if (!(alignment1.getUnclippedStart() == alignment2.getUnclippedStart())) {
                areEqual = false;
                System.out.println("Unclipped Start is not equal");
            }
            if (!(alignment1.getUnclippedEnd() == alignment2.getUnclippedEnd())) {
                areEqual = false;
                System.out.println("Unclipped End is not equal: " + alignment1.getUnclippedEnd() + " != " + alignment2.getUnclippedEnd());
            }
            if (!(alignment1.getLength() == alignment2.getLength())) {
                areEqual = false;
                System.out.println("Length is not equal");
            }
            if (!(alignment1.getMappingQuality() == alignment2.getMappingQuality())) {
                areEqual = false;
                System.out.println("MappingQuality is not equal");
            }
            if (!alignment1.getQualities().equals(alignment2.getQualities())) {
                areEqual = false;
                System.out.println("qualities is not equal");
            }
            if (!(alignment1.getMateReferenceName() == alignment2.getMateReferenceName())) {
                areEqual = false;
                System.out.println("MateReferenceName is not equal");
            }
            if (!(alignment1.getMateAlignmentStart() == alignment2.getMateAlignmentStart())) {
                areEqual = false;
                System.out.println("MateAlignmentStart not equal");
            }

/*
            if (alignment1.getReadSequence() == null ^ alignment2.getReadSequence() == null) { // only one is null
                areEqual = false;
                System.out.println("only one sequence is null: " + (alignment1.getReadSequence() == null) + ", " + (alignment2.getReadSequence() == null));
            } else if (alignment1.getReadSequence() != null && !Arrays.equals(alignment1.getReadSequence(), alignment2.getReadSequence())) {  // both are not null and different
                areEqual = false;
                System.out.println(alignment1.getReadSequence());
                System.out.println(alignment2.getReadSequence());
            }
*/

            if (alignment1.getAttributes() == null) {
                if (alignment2.getAttributes() != null) {
                    areEqual = false;
                    System.out.println(" origin Attributes is null and dest is not null");
                }
            } else {
                if (alignment2.getAttributes() == null) {
                    areEqual = false;
                    System.out.println("origin Attributes is not null and dest is null");
                } else {
                    if (!alignment1.getAttributes().equals(alignment2.getAttributes())) {
                        areEqual = false;
                        System.out.println("Attributes is not equal");
                    }
                }
            }

            if (alignment1.getDifferences() == null) {
                if (alignment2.getDifferences() != null) {
                    areEqual = false;
                    System.out.println(" origin Differences is null and dest is not null");
                }
            } else {
                if (alignment2.getDifferences() == null) {
                    areEqual = false;
                    System.out.println("origin Differences is not null and dest is null");
                } else {
                    if (!alignment1.getDifferences().equals(alignment2.getDifferences())) {
                        areEqual = false;
                        System.out.println("Differences is not equal");
                    }
                }
            }

            if (!(alignment1.getFlags() == alignment2.getFlags())) {
                areEqual = false;
                System.out.println("flags is not equal");
            }
        }
        if (!areEqual) {
            System.out.println("ProtoHelper failed!");
        }
        return areEqual;
    }
}
