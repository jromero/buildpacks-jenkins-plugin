package io.jenkins.plugins.cmd;

import java.io.File;
import java.nio.file.Paths;

import io.jenkins.plugins.lifecycle.api.*;

public class Flags {

    static String execExt = ""; // ??? 
    static String DeprecationModeWarn = ""; // ??? 
    static String rootDir = Paths.get(System.getProperty("user.dir")).toString();

	public static String DefaultAppDir          = Paths.get(rootDir, "workspace").toString();
	public static String DefaultBuildpacksDir   = Paths.get(rootDir, "cnb", "buildpacks").toString();
	public static String DefaultDeprecationMode = DeprecationModeWarn;
	public static String DefaultLauncherPath    = Paths.get(rootDir, "cnb", "lifecycle", "launcher" + execExt).toString();
	public static String DefaultLayersDir       = Paths.get(rootDir, "layers").toString();
	public static String DefaultLogLevel        = "info";
	public static String DefaultPlatformAPI     = "0.3";
	public static String DefaultPlatformDir     = Paths.get(rootDir, "platform").toString();
	public static String DefaultProcessType     = "web";
	public static String DefaultStackPath       = Paths.get(rootDir, "cnb", "stack.toml").toString();

	public static String DefaultAnalyzedFile        = "analyzed.toml";
	public static String DefaultGroupFile           = "group.toml";
	public static String DefaultOrderFile           = "order.toml";
	public static String DefaultPlanFile            = "plan.toml";
	public static String DefaultProjectMetadataFile = "project-metadata.toml";
	public static String DefaultReportFile          = "report.toml";

	public static String PlaceholderAnalyzedPath        = Paths.get("<layers>", DefaultAnalyzedFile).toString();
	public static String PlaceholderGroupPath           = Paths.get("<layers>", DefaultGroupFile).toString();
	public static String PlaceholderPlanPath            = Paths.get("<layers>", DefaultPlanFile).toString();
	public static String PlaceholderProjectMetadataPath = Paths.get("<layers>", DefaultProjectMetadataFile).toString();
	public static String PlaceholderReportPath          = Paths.get("<layers>", DefaultReportFile).toString();
	public static String PlaceholderOrderPath           = Paths.get("<layers>", DefaultOrderFile).toString();

	static final String EnvAnalyzedPath        = "CNB_ANALYZED_PATH";
	static final String EnvAppDir              = "CNB_APP_DIR";
	static final String EnvBuildpacksDir       = "CNB_BUILDPACKS_DIR";
	static final String EnvCacheDir            = "CNB_CACHE_DIR";
	static final String EnvCacheImage          = "CNB_CACHE_IMAGE";
	static final String EnvDeprecationMode     = "CNB_DEPRECATION_MODE";
	static final String EnvGID                 = "CNB_GROUP_ID";
	static final String EnvGroupPath           = "CNB_GROUP_PATH";
	static final String EnvLaunchCacheDir      = "CNB_LAUNCH_CACHE_DIR";
	static final String EnvLayersDir           = "CNB_LAYERS_DIR";
	static final String EnvLogLevel            = "CNB_LOG_LEVEL";
	static final String EnvNoColor             = "CNB_NO_COLOR"; // defaults to false
	static final String EnvOrderPath           = "CNB_ORDER_PATH";
	static final String EnvPlanPath            = "CNB_PLAN_PATH";
	static final String EnvPlatformAPI         = "CNB_PLATFORM_API";
	static final String EnvPlatformDir         = "CNB_PLATFORM_DIR";
	static final String EnvPreviousImage       = "CNB_PREVIOUS_IMAGE";
	static final String EnvProcessType         = "CNB_PROCESS_TYPE";
	static final String EnvProjectMetadataPath = "CNB_PROJECT_METADATA_PATH";
	static final String EnvReportPath          = "CNB_REPORT_PATH";
	static final String EnvRunImage            = "CNB_RUN_IMAGE";
	static final String EnvSkipLayers          = "CNB_ANALYZE_SKIP_LAYERS"; // defaults to false
	static final String EnvSkipRestore         = "CNB_SKIP_RESTORE";        // defaults to false
	static final String EnvStackPath           = "CNB_STACK_PATH";
	static final String EnvUID                 = "CNB_USER_ID";
	static final String EnvUseDaemon           = "CNB_USE_DAEMON"; // defaults to false

	public static String defaultPath(String fileName, String platformAPI, String layersDir) {
		if (Api.MustParse(platformAPI).Compare(Api.MustParse("0.5")) < 0 || layersDir == "") {
			// prior to platform api 0.5, the default directory was the working dir.
			// layersDir is unset when this call comes from the rebaser - will be fixed as part of https://github.com/buildpacks/spec/issues/156
			return Paths.get(".", fileName).toString();
		}
		return Paths.get(layersDir, fileName).toString(); // starting from platform api 0.5, the default directory is the layers dir.
	}

	public static String DefaultGroupPath(String platformAPI, String layersDir) {
		return defaultPath(DefaultGroupFile, platformAPI, layersDir);
	}

	public static String DefaultPlanPath(String platformAPI, String layersDir) {
		return defaultPath(DefaultPlanFile, platformAPI, layersDir);
	}

	public static String DefaultOrderPath(String platformAPI, String layersDir) {
		String cnbOrderPath = Paths.get(rootDir, "cnb", "order.toml").toString();
	
		// prior to Platform API 0.6, the default is /cnb/order.toml
		if (Api.MustParse(platformAPI).Compare(Api.MustParse("0.6")) < 0) {
			return cnbOrderPath;
		}
	
		// the default is /<layers>/order.toml or /cnb/order.toml if not present
		String layersOrderPath = Paths.get(layersDir, "order.toml").toString();
		if (!(new File(layersOrderPath)).exists()) {
			return cnbOrderPath;
		}
		return layersOrderPath;
	}

	

}
