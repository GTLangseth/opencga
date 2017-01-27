package org.opencb.opencga.app.cli.analysis.options;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.beust.jcommander.converters.CommaParameterSplitter;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.opencga.app.cli.GeneralCliOptions;
import org.opencb.opencga.app.cli.GeneralCliOptions.DataModelOptions;
import org.opencb.opencga.app.cli.GeneralCliOptions.NumericOptions;
import org.opencb.opencga.storage.app.cli.client.options.StorageVariantCommandOptions;
import org.opencb.opencga.storage.core.variant.annotation.annotators.VariantAnnotatorFactory;

import java.util.List;

/**
 * Created by pfurio on 23/11/16.
 */
@Parameters(commandNames = {"variant"}, commandDescription = "Variant commands")
public class VariantCommandOptions {

    public VariantIndexCommandOptions indexVariantCommandOptions;
//    public QueryVariantCommandOptionsOld queryVariantCommandOptionsOld;
    public VariantQueryCommandOptions queryVariantCommandOptions;
    public VariantStatsCommandOptions statsVariantCommandOptions;
    public VariantAnnotateCommandOptions annotateVariantCommandOptions;
    public VariantExportStatsCommandOptions exportVariantStatsCommandOptions;
    public VariantImportCommandOptions importVariantCommandOptions;
    public VariantIbsCommandOptions ibsVariantCommandOptions;

    public JCommander jCommander;
    public GeneralCliOptions.CommonCommandOptions commonCommandOptions;
    public DataModelOptions commonDataModelOptions;
    public NumericOptions commonNumericOptions;

    public VariantCommandOptions(GeneralCliOptions.CommonCommandOptions commonCommandOptions, DataModelOptions dataModelOptions,
                                 NumericOptions numericOptions, JCommander jCommander) {
        this.commonCommandOptions = commonCommandOptions;
        this.commonDataModelOptions = dataModelOptions;
        this.commonNumericOptions = numericOptions;
        this.jCommander = jCommander;

        this.indexVariantCommandOptions = new VariantIndexCommandOptions();
//        this.queryVariantCommandOptionsOld = new QueryVariantCommandOptionsOld();
        this.queryVariantCommandOptions = new VariantQueryCommandOptions();
        this.statsVariantCommandOptions = new VariantStatsCommandOptions();
        this.annotateVariantCommandOptions = new VariantAnnotateCommandOptions();
        this.exportVariantStatsCommandOptions = new VariantExportStatsCommandOptions();
        this.importVariantCommandOptions = new VariantImportCommandOptions();
        this.ibsVariantCommandOptions = new VariantIbsCommandOptions();
    }

    @Parameters(commandNames = {"index"}, commandDescription = "Index variants file")
    public class VariantIndexCommandOptions extends GeneralCliOptions.StudyOption {

        @ParametersDelegate
        public StorageVariantCommandOptions.GenericVariantIndexOptions genericVariantIndexOptions = new StorageVariantCommandOptions.GenericVariantIndexOptions();

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"--file"}, description = "CSV of file ids to be indexed", required = true, arity = 1)
        public String fileId = null;

        @Parameter(names = {"--transformed-files"}, description = "CSV of paths corresponding to the location of the transformed files.",
                arity = 1)
        public String transformedPaths = null;

        @Parameter(names = {"-o", "--outdir"}, description = "Output directory outside catalog boundaries.", required = true, arity = 1)
        public String outdir = null;

        @Parameter(names = {"--path"}, description = "Path within catalog boundaries where the results will be stored. If not present, "
                + "transformed files will not be registered in catalog.", arity = 1)
        public String catalogPath = null;
    }


    @Deprecated
    public class IndexVariantCommandOptionsOld extends GeneralCliOptions.StudyOption {

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

//        @ParametersDelegate
//        public AnalysisCliOptionsParser.JobCommand job = new AnalysisCliOptionsParser.JobCommand();

//
//        @Parameter(names = {"-i", "--input"}, description = "File to index in the selected backend", required = true, variableArity = true)
//        public List<String> input;

//        @Parameter(names = {"-o", "--outdir"}, description = "Directory where output files will be saved (optional)", arity = 1)
//        public String outdir;

//        @Parameter(names = {"--file-id"}, description = "Unique ID for the file", required = true, arity = 1)
//        public String fileId;


//        @Parameter(names = {"--study-id"}, description = "Unque ID for the study", arity = 1)
//        public long studyId;

        @Parameter(names = {"--file"}, description = "CSV of file ids to be indexed", required = true, arity = 1)
        public String fileId = null;

        @Parameter(names = {"--transformed-files"}, description = "CSV of paths corresponding to the location of the transformed files.",
                arity = 1)
        public String transformedPaths = null;

        @Parameter(names = {"-o", "--outdir"}, description = "Output directory outside catalog boundaries.", required = true, arity = 1)
        public String outdir = null;

        @Parameter(names = {"--path"}, description = "Path within catalog boundaries where the results will be stored. If not present, "
                + "transformed files will not be registered in catalog.", arity = 1)
        public String catalogPath = null;


        //////
        // Commons

        @Parameter(names = {"--transform"}, description = "If present it only runs the transform stage, no load is executed")
        public boolean transform = false;

        @Parameter(names = {"--load"}, description = "If present only the load stage is executed, transformation is skipped")
        public boolean load = false;

        @Parameter(names = {"--exclude-genotypes"}, description = "Index excluding the genotype information")
        public boolean excludeGenotype = false;

        @Parameter(names = {"--include-extra-fields"}, description = "Index including other genotype fields [CSV]")
        public String extraFields = "";

        @Parameter(names = {"--aggregated"}, description = "Select the type of aggregated VCF file: none, basic, EVS or ExAC", arity = 1)
        public VariantSource.Aggregation aggregated = VariantSource.Aggregation.NONE;

        @Parameter(names = {"--aggregation-mapping-file"}, description = "File containing population names mapping in an aggregated VCF " +
                "file")
        public String aggregationMappingFile = null;

        @Parameter(names = {"--gvcf"}, description = "The input file is in gvcf format")
        public boolean gvcf;

        @Parameter(names = {"--bgzip"}, description = "[PENDING] The input file is in bgzip format")
        public boolean bgzip;

        @Parameter(names = {"--calculate-stats"}, description = "Calculate indexed variants statistics after the load step")
        public boolean calculateStats = false;

        @Parameter(names = {"--annotate"}, description = "Annotate indexed variants after the load step")
        public boolean annotate = false;

        @Parameter(names = {"--annotator"}, description = "Annotation source {cellbase_rest, cellbase_db_adaptor}")
        public VariantAnnotatorFactory.AnnotationSource annotator = null;

        @Parameter(names = {"--overwrite-annotations"}, description = "Overwrite annotations in variants already present")
        public boolean overwriteAnnotations;

        @Parameter(names = {"--resume"}, description = "Resume a previously failed indexation", arity = 0)
        public boolean resume;
    }

    @Deprecated
    public class QueryVariantCommandOptionsOld {

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"--studies"}, description = "Study identifiers", required = true, arity = 1)
        public String studies;

        @Parameter(names = {"--include"}, description = "Comma separated list of fields to be included in the response", arity = 1)
        public String include;

        @Parameter(names = {"--exclude"}, description = "Comma separated list of fields to be excluded from the response", arity = 1)
        public String exclude;

        @Parameter(names = {"--skip"}, description = "Number of results to skip", arity = 1)
        public String skip;

        @Parameter(names = {"--limit"}, description = "Maximum number of results to be returned", arity = 1)
        public String limit;

        @Parameter(names = {"--variant-ids"}, description = "List of variant ids", arity = 1)
        public String ids;

        @Parameter(names = {"--region"}, description = "List of regions: {chr}:{start}-{end}", arity = 1)
        public String region;

        @Parameter(names = {"--chromosome"}, description = "List of chromosomes", arity = 1)
        public String chromosome;

        @Parameter(names = {"--gene"}, description = "List of genes", arity = 1)
        public String gene;

        @Parameter(names = {"--type"}, description = "Variant types: [SNV, MNV, INDEL, SV, CNV]", arity = 1)
        public VariantType type;

        @Parameter(names = {"--reference"}, description = "Reference allele", arity = 1)
        public String reference;

        @Parameter(names = {"--alternate"}, description = "Main alternate allele", arity = 1)
        public String alternate;

        @Parameter(names = {"--returned-studies"}, description = "List of studies to be returned", arity = 1)
        public String returnedStudies;

        @Parameter(names = {"--returned-samples"}, description = "List of samples to be returned", arity = 1)
        public String returnedSamples;

        @Parameter(names = {"--returned-files"}, description = "List of files to be returned.", arity = 1)
        public String returnedFiles;

        @Parameter(names = {"--files"}, description = "Variants in specific files", arity = 1)
        public String files;

        @Parameter(names = {"--maf"}, description = "Minor Allele Frequency: [{study:}]{cohort}[<|>|<=|>=]{number}",
                arity = 1)
        public String maf;

        @Parameter(names = {"--mgf"}, description = "Minor Genotype Frequency: [{study:}]{cohort}[<|>|<=|>=]{number}",
                arity = 1)
        public String mgf;

        @Parameter(names = {"--missing-alleles"}, description = "Number of missing alleles: [{study:}]{cohort}[<|>|<=|>=]{number}",
                arity = 1)
        public String missingAlleles;

        @Parameter(names = {"--missing-genotypes"}, description = "Number of missing genotypes: [{study:}]{cohort}[<|>|<=|>=]{number}",
                arity = 1)
        public String missingGenotypes;

//        @Parameter(names = {"--annotation-exists"}, description = "Specify if the variant annotation must exists.",
//                arity = 0)
//        public boolean annotationExists;

        @Parameter(names = {"--genotype"}, description = "Samples with a specific genotype: {samp_1}:{gt_1}(,{gt_n})*(;{samp_n}:{gt_1}"
                + "(,{gt_n})*)* e.g. HG0097:0/0;HG0098:0/1,1/1", arity = 1)
        public String genotype;

        @Parameter(names = {"--annot-ct"}, description = "Consequence type SO term list. e.g. missense_variant,stop_lost or SO:0001583,SO:0001578",
                arity = 1)
        public String annot_ct;

        @Parameter(names = {"--annot-xref"}, description = "XRef", arity = 1)
        public String annot_xref;

        @Parameter(names = {"--annot-biotype"}, description = "Biotype", arity = 1)
        public String annot_biotype;

        @Parameter(names = {"--polyphen"}, description = "Polyphen, protein substitution score. [<|>|<=|>=]{number} or [~=|=|]{description}"
                + " e.g. <=0.9 , =benign", arity = 1)
        public String polyphen;

        @Parameter(names = {"--sift"}, description = "Sift, protein substitution score. [<|>|<=|>=]{number} or [~=|=|]{description} "
                + "e.g. >0.1 , ~=tolerant", arity = 1)
        public String sift;

        @Parameter(names = {"--conservation"}, description = "VConservation score: {conservation_score}[<|>|<=|>=]{number} "
                + "e.g. phastCons>0.5,phylop<0.1,gerp>0.1", arity = 1)
        public String conservation;

        @Parameter(names = {"--annot-population-maf"}, description = "Population minor allele frequency: "
                + "{study}:{population}[<|>|<=|>=]{number}", arity = 1)
        public String annotPopulationMaf;

        @Parameter(names = {"--alternate-frequency"}, description = "Alternate Population Frequency: "
                + "{study}:{population}[<|>|<=|>=]{number}", arity = 1)
        public String alternate_frequency;

        @Parameter(names = {"--reference-frequency"}, description = "Reference Population Frequency:"
                + " {study}:{population}[<|>|<=|>=]{number}", arity = 1)
        public String reference_frequency;

        @Parameter(names = {"--annot-transcription-flags"}, description = "List of transcript annotation flags. "
                + "e.g. CCDS, basic, cds_end_NF, mRNA_end_NF, cds_start_NF, mRNA_start_NF, seleno", arity = 1)
        public String transcriptionFlags;

        @Parameter(names = {"--annot-gene-trait-id"}, description = "List of gene trait association id. e.g. \"umls:C0007222\" , "
                + "\"OMIM:269600\"", arity = 1)
        public String geneTraitId;


        @Parameter(names = {"--annot-gene-trait-name"}, description = "List of gene trait association names. "
                + "e.g. \"Cardiovascular Diseases\"", arity = 1)
        public String geneTraitName;

        @Parameter(names = {"--annot-hpo"}, description = "List of HPO terms. e.g. \"HP:0000545\"", arity = 1)
        public String hpo;

        @Parameter(names = {"--annot-go"}, description = "List of GO (Genome Ontology) terms. e.g. \"GO:0002020\"", arity = 1)
        public String go;

        @Parameter(names = {"--annot-expression"}, description = "List of tissues of interest. e.g. \"tongue\"", arity = 1)
        public String expression;

        @Parameter(names = {"--annot-protein-keywords"}, description = "List of protein variant annotation keywords",
                arity = 1)
        public String proteinKeyword;

        @Parameter(names = {"--annot-drug"}, description = "List of drug names", arity = 1)
        public String drug;

        @Parameter(names = {"--annot-functional-score"}, description = "Functional score: {functional_score}[<|>|<=|>=]{number} "
                + "e.g. cadd_scaled>5.2 , cadd_raw<=0.3", arity = 1)
        public String functionalScore;

        @Parameter(names = {"--unknown-genotype"}, description = "Returned genotype for unknown genotypes. Common values: [0/0, 0|0, ./.]",
                arity = 1)
        public String unknownGenotype;

        @Parameter(names = {"--samples-metadata"}, description = "Returns the samples metadata group by studyId, instead of the variants",
                arity = 0)
        public boolean samplesMetadata;

        @Parameter(names = {"--sort"}, description = "Sort the results", arity = 0)
        public boolean sort;

        @Parameter(names = {"--group-by"}, description = "Group variants by: [ct, gene, ensemblGene]", arity = 1)
        public String groupBy;

        @Parameter(names = {"--count"}, description = "Count results", arity = 0)
        public boolean count;

        @Parameter(names = {"--histogram"}, description = "Calculate histogram. Requires one region.", arity = 0)
        public boolean histogram;

        @Parameter(names = {"--interval"}, description = "Histogram interval size. Default:2000", arity = 1)
        public String interval;

        @Parameter(names = {"--mode"}, description = "Communication mode. grpc|rest|auto.")
        public String mode = "auto";

    }

    @Parameters(commandNames = {"query"}, commandDescription = "Search over indexed variants")
    public class VariantQueryCommandOptions {

        @ParametersDelegate
        public StorageVariantCommandOptions.GenericVariantQueryOptions genericVariantQueryOptions = new StorageVariantCommandOptions.GenericVariantQueryOptions();

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

        @ParametersDelegate
        public DataModelOptions dataModelOptions = commonDataModelOptions;

        @ParametersDelegate
        public NumericOptions numericOptions = commonNumericOptions;

        @Parameter(names = {"--mode"}, description = "Communication mode. grpc|rest|auto.")
        public String mode = "auto";

        @Parameter(names = {"-o", "--output"}, description = "Output file. [STDOUT]", arity = 1)
        public String output;
    }

    @Parameters(commandNames = {"stats"}, commandDescription = "Create and load stats into a database.")
    public class VariantStatsCommandOptions {

        @ParametersDelegate
        public StorageVariantCommandOptions.GenericVariantStatsOptions genericVariantStatsOptions = new StorageVariantCommandOptions.GenericVariantStatsOptions();

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"--cohort-ids"}, description = "Cohort Ids for the cohorts to be calculated.")
        public String cohortIds;

        @Parameter(names = {"-o", "--outdir"}, description = "Output directory outside catalog boundaries.", required = true, arity = 1)
        public String outdir = null;

        @Parameter(names = {"--path"}, description = "Path within catalog boundaries where the results will be stored. If not present, "
                + "transformed files will not be registered in catalog.", arity = 1)
        public String catalogPath = null;
    }

    public class StatsVariantStatsCommandOptionsOld { //extends AnalysisCliOptionsParser.CatalogDatabaseCommandOptions {

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

//        @ParametersDelegate
//        public AnalysisCliOptionsParser.JobCommand job = new AnalysisCliOptionsParser.JobCommand();

//        @Parameter(names = {"--create"}, description = "Run only the creation of the stats to a file")
//        public boolean create = false;
//
//        @Parameter(names = {"--load"}, description = "Load the stats from an already existing FILE directly into the database. FILE is a "
//                + "prefix with structure <INPUT_FILENAME>.<TIME>")
//        public boolean load = false;

        @Parameter(names = {"--overwrite-stats"}, description = "Overwrite stats in variants already present")
        public boolean overwriteStats = false;

        @Parameter(names = {"--region"}, description = "[PENDING] Region to calculate.")
        public String region;

        @Parameter(names = {"--update-stats"}, description = "Calculate stats just for missing positions. "
                + "Assumes that existing stats are correct")
        public boolean updateStats = false;

        @Parameter(names = {"-s", "--study-id"}, description = "Unique ID for the study where the file is classified", required = true,
                arity = 1)
        public String studyId;

        @Parameter(names = {"-f", "--file-id"}, description = "Calculate stats only for the selected file", arity = 1)
        public String fileId;

        @Parameter(names = {"--cohort-ids"}, description = "Cohort Ids for the cohorts to be calculated.")
        public String cohortIds;

        // FIXME: Hidden?
        @Parameter(names = {"--output-filename"}, description = "Output file name. Default: database name", arity = 1)
        public String fileName;

//        @Parameter(names = {"--outdir-id"}, description = "Output directory", arity = 1)
//        public String outdirId;

        @Parameter(names = {"-o", "--outdir"}, description = "Output directory outside catalog boundaries.", required = true, arity = 1)
        public String outdir = null;

        @Parameter(names = {"--path"}, description = "Path within catalog boundaries where the results will be stored. If not present, "
                + "transformed files will not be registered in catalog.", arity = 1)
        public String catalogPath = null;

        @Parameter(names = {"--aggregated"}, description = "Select the type of aggregated VCF file: none, basic, EVS or ExAC", arity = 1)
        public VariantSource.Aggregation aggregated = VariantSource.Aggregation.NONE;

        @Parameter(names = {"--aggregation-mapping-file"}, description = "File containing population names mapping in an aggregated VCF file")
        public String aggregationMappingFile;

        @Parameter(names = {"--resume"}, description = "Resume a previously failed stats calculation", arity = 0)
        public boolean resume;
    }

    @Parameters(commandNames = {"annotate"}, commandDescription = "Create and load variant annotations into the database")
    public class VariantAnnotateCommandOptions extends GeneralCliOptions.StudyOption {

        @ParametersDelegate
        public StorageVariantCommandOptions.GenericVariantAnnotateOptions genericVariantAnnotateOptions = new StorageVariantCommandOptions.GenericVariantAnnotateOptions();

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"-p", "--project-id"}, description = "Project to annotate.", arity = 1)
        public String project;

//        @Parameter(names = {"-s", "--study-id"}, description = "Studies to annotate. Must be in the same database.", arity = 1)
//        public String study;

        @Parameter(names = {"-o", "--outdir"}, description = "Output directory outside catalog boundaries.", required = true, arity = 1)
        public String outdir;

        @Parameter(names = {"--path"}, description = "Path within catalog boundaries where the results will be stored. If not present, "
                + "transformed files will not be registered in catalog.", arity = 1)
        public String catalogPath;
    }

    @Deprecated
    public class AnnotateVariantCommandOptionsOld { //extends AnalysisCliOptionsParser.CatalogDatabaseCommandOptions {

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

//        @ParametersDelegate
//        public AnalysisCliOptionsParser.JobCommand job = new AnalysisCliOptionsParser.JobCommand();

        @Parameter(names = {"-p", "--project-id"}, description = "Project to annotate.", arity = 1)
        public String project;

        @Parameter(names = {"-s", "--study-id"}, description = "Studies to annotate. Must be in the same database.", arity = 1)
        public String studyId;

        @Parameter(names = {"-o", "--outdir"}, description = "Output directory outside catalog boundaries.", required = true, arity = 1)
        public String outdir = null;

        @Parameter(names = {"--path"}, description = "Path within catalog boundaries where the results will be stored. If not present, "
                + "transformed files will not be registered in catalog.", arity = 1)
        public String catalogPath;

        /////////
        // Generic

        @Parameter(names = {"--create"}, description = "Run only the creation of the annotations to a file (specified by --output-filename)")
        public boolean create = false;

        @Parameter(names = {"--load"}, description = "Run only the load of the annotations into the DB from FILE. "
                + "Can be a file from catalog or a local file.")
        public String load = null;

        @Parameter(names = {"--custom-name"}, description = "Provide a name to the custom annotation")
        public String customAnnotationKey = null;

        @Parameter(names = {"--annotator"}, description = "Annotation source {cellbase_rest, cellbase_db_adaptor}")
        public VariantAnnotatorFactory.AnnotationSource annotator;

        @Parameter(names = {"--overwrite-annotations"}, description = "Overwrite annotations in variants already present")
        public boolean overwriteAnnotations = false;

        @Parameter(names = {"--output-filename"}, description = "Output file name. Default: dbName", arity = 1)
        public String fileName;

        @Parameter(names = {"--species"}, description = "Species. Default hsapiens", arity = 1)
        public String species = "hsapiens";

        @Parameter(names = {"--assembly"}, description = "Assembly. Default GRCh37", arity = 1)
        public String assembly = "GRCh37";

        @Parameter(names = {"--filter-region"}, description = "Comma separated region filters", splitter = CommaParameterSplitter.class)
        public List<String> filterRegion;

        @Parameter(names = {"--filter-chromosome"}, description = "Comma separated chromosome filters", splitter = CommaParameterSplitter.class)
        public List<String> filterChromosome;

        @Parameter(names = {"--filter-gene"}, description = "Comma separated gene filters", splitter = CommaParameterSplitter.class)
        public String filterGene;

        @Parameter(names = {"--filter-annot-consequence-type"}, description = "Comma separated annotation consequence type filters",
                splitter = CommaParameterSplitter.class)
        public List filterAnnotConsequenceType = null; // TODO will receive CSV, only available when create annotations

    }

    @Parameters(commandNames = {"export-frequencies"}, commandDescription = "Export calculated variant stats and frequencies")
    public class VariantExportStatsCommandOptions {

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

        @ParametersDelegate
        public NumericOptions numericOptions = commonNumericOptions;

//        @ParametersDelegate
//        public QueryCommandOptions queryOptions = new QueryCommandOptions();

//        @Parameter(names = {"--of", "--output-format"}, description = "Output format: vcf, vcf.gz, tsv, tsv.gz, cellbase, cellbase.gz, json or json.gz", arity = 1)
//        public String outputFormat = "tsv";

        @Parameter(names = {"-r", "--region"}, description = "CSV list of regions: {chr}[:{start}-{end}]. example: 2,3:1000000-2000000",
                required = false)
        public String region;

        @Parameter(names = {"--region-file"}, description = "GFF File with regions")
        public String regionFile;

        @Parameter(names = {"-g", "--gene"}, description = "CSV list of genes")
        public String gene;

        @Parameter(names = {"-s", "--study"}, description = "A comma separated list of studies to be returned")
        public String studies;

        @Parameter(names = {"-o", "--output"}, description = "Output file. [STDOUT]", arity = 1)
        public String output;
    }

    @Parameters(commandNames = {"import"}, commandDescription = "Import a variants dataset into an empty study")
    public class VariantImportCommandOptions {

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;

        @Parameter(names = {"-s", "--study"}, description = "Study where to load the variants", required = true)
        public String study;

        @Parameter(names = {"-i", "--input"}, description = "Variants input file in avro format", required = true)
        public String input;

    }

    @Parameters(commandNames = {"ibs"}, commandDescription = "[PENDING] ")
    public class VariantIbsCommandOptions { //extends CatalogDatabaseCommandOptions {

        @ParametersDelegate
        public GeneralCliOptions.CommonCommandOptions commonOptions = commonCommandOptions;
    }
}
