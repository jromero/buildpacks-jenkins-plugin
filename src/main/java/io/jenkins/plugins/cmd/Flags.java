package io.jenkins.plugins.cmd;

public class Flags {
    
	public static String DefaultAppDir          = filepath.Join(rootDir, "workspace")
	public static String DefaultBuildpacksDir   = filepath.Join(rootDir, "cnb", "buildpacks")
	public static String DefaultDeprecationMode = DeprecationModeWarn
	public static String DefaultLauncherPath    = filepath.Join(rootDir, "cnb", "lifecycle", "launcher"+execExt)
	public static String DefaultLayersDir       = filepath.Join(rootDir, "layers")
	public static String DefaultLogLevel        = "info"
	public static String DefaultPlatformAPI     = "0.3"
	public static String DefaultPlatformDir     = filepath.Join(rootDir, "platform")
	public static String DefaultProcessType     = "web"
	public static String DefaultStackPath       = filepath.Join(rootDir, "cnb", "stack.toml")

	public static String DefaultAnalyzedFile        = "analyzed.toml"
	public static String DefaultGroupFile           = "group.toml"
	public static String DefaultOrderFile           = "order.toml"
	public static String DefaultPlanFile            = "plan.toml"
	public static String DefaultProjectMetadataFile = "project-metadata.toml"
	public static String DefaultReportFile          = "report.toml"

	public static String PlaceholderAnalyzedPath        = filepath.Join("<layers>", DefaultAnalyzedFile)
	public static String PlaceholderGroupPath           = filepath.Join("<layers>", DefaultGroupFile)
	public static String PlaceholderPlanPath            = filepath.Join("<layers>", DefaultPlanFile)
	public static String PlaceholderProjectMetadataPath = filepath.Join("<layers>", DefaultProjectMetadataFile)
	public static String PlaceholderReportPath          = filepath.Join("<layers>", DefaultReportFile)
	public static String PlaceholderOrderPath           = filepath.Join("<layers>", DefaultOrderFile)

	static final String EnvAnalyzedPath        = "CNB_ANALYZED_PATH"
	static final String EnvAppDir              = "CNB_APP_DIR"
	static final String EnvBuildpacksDir       = "CNB_BUILDPACKS_DIR"
	static final String EnvCacheDir            = "CNB_CACHE_DIR"
	static final String EnvCacheImage          = "CNB_CACHE_IMAGE"
	static final String EnvDeprecationMode     = "CNB_DEPRECATION_MODE"
	static final String EnvGID                 = "CNB_GROUP_ID"
	static final String EnvGroupPath           = "CNB_GROUP_PATH"
	static final String EnvLaunchCacheDir      = "CNB_LAUNCH_CACHE_DIR"
	static final String EnvLayersDir           = "CNB_LAYERS_DIR"
	static final String EnvLogLevel            = "CNB_LOG_LEVEL"
	static final String EnvNoColor             = "CNB_NO_COLOR" // defaults to false
	static final String EnvOrderPath           = "CNB_ORDER_PATH"
	static final String EnvPlanPath            = "CNB_PLAN_PATH"
	static final String EnvPlatformAPI         = "CNB_PLATFORM_API"
	static final String EnvPlatformDir         = "CNB_PLATFORM_DIR"
	static final String EnvPreviousImage       = "CNB_PREVIOUS_IMAGE"
	static final String EnvProcessType         = "CNB_PROCESS_TYPE"
	static final String EnvProjectMetadataPath = "CNB_PROJECT_METADATA_PATH"
	static final String EnvReportPath          = "CNB_REPORT_PATH"
	static final String EnvRunImage            = "CNB_RUN_IMAGE"
	static final String EnvSkipLayers          = "CNB_ANALYZE_SKIP_LAYERS" // defaults to false
	static final String EnvSkipRestore         = "CNB_SKIP_RESTORE"        // defaults to false
	static final String EnvStackPath           = "CNB_STACK_PATH"
	static final String EnvUID                 = "CNB_USER_ID"
	static final String EnvUseDaemon           = "CNB_USE_DAEMON" // defaults to false

}
