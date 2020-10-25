declare module '@capacitor/core' {
  interface PluginRegistry {
    DownloadPlugin: DownloadPlugin;
  }
}

export interface DownloadPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
