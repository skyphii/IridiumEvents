# IridiumEvents

## Using the API in your plugin
Add the dependency to your pom.xml (make sure the version is up to date!)
You may need to build IridiumEvents on your local machine (or add it to your local maven repo manually). I can add more details about this in the future.
```xml
<dependency>
      <groupId>dev.skyphi</groupId>
      <artifactId>iridiumevents</artifactId>
      <version>1.0-SNAPSHOT</version>
</dependency>
```

Now we need to add the IridiumEvents plugin as a depedency in your plugin.yml. You can add it to the `depend` or `softdepend` list depending on your implementation. When soft-depending, you'll need to make sure to handle cases where the plugin is missing.
```yml
softdepend: [IridiumEvents]
```

Next, we'll go to your plugin's main file and set up access to the API.
```java
public static IridiumAPI API;

@Override
public void onEnable() {
    API = getServer().getServicesManager().load(IridiumAPI.class);
    if (API == null) {
        getLogger().warning("IridiumAPI not found! Statistics will not be tracked. Make sure the IridiumEvents plugin is installed.");
    }
}
```

That should do it! You should now be able to access the IridiumAPI from your plugin. Here's a simple kill/death tracking example:
```java
public class DeathListener implements Listener {
    
    @EventHandler
    public void on(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        final Player player = (Player) event.getEntity();
        if (player.getHealth() - event.getFinalDamage() > 0) return;

        IridiumCTF.API.addStatistic("UNIQUE_PLUGIN_IDENTIFIER", "deaths", player.getUniqueId(), 1);

        if (event instanceof EntityDamageByEntityEvent) {
            Entity killer = getDamager((EntityDamageByEntityEvent) event);

            if (killer instanceof Player) {
                IridiumCTF.API.addStatistic("UNIQUE_PLUGIN_IDENTIFIER", "kills", ((Player) killer).getUniqueId(), 1);
            }
        }
    }

}
```

## "UNIQUE_PLUGIN_IDENTIFIER" is the Golden Rule!
Each plugin that uses the IridiumAPI for statistics *MUST* have a unique string identifier that it provides with each statistic request. This key is used for the database table name, so if you use an existing one you'll end up affecting the existing statistics table. I figure this shouldn't be an issue for us but it's definitely worth noting!
