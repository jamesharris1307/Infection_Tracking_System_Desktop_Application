//package com.example.infection_monitoring_system_desktop_application.Unit.Manager;
//
//import com.example.infection_monitoring_system_desktop_application.Manager.AppContext;
//import com.example.infection_monitoring_system_desktop_application.Manager.LanguageManager;
//import com.example.infection_monitoring_system_desktop_application.Manager.SceneManager;
//import com.example.infection_monitoring_system_desktop_application.Manager.ThemeManager;
//import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.PreferencesException;
//import com.example.infection_monitoring_system_desktop_application.Util.Exceptions.SceneLoadException;
//import javafx.application.Platform;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.stage.Stage;
//import org.junit.jupiter.api.*;
//import org.mockito.MockedConstruction;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.net.URL; // Added import
//import java.util.Locale;
//import java.util.ResourceBundle;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class SceneManagerTest {
//
//    private Stage mockStage;
//    private final String TEST_FXML_PATH = "/Views/TestView.fxml";
//    // Define a mock URL to prevent the "Location is not set" error
//    private final URL MOCKED_URL = getClass().getResource("/Views/Dummy.fxml");
//
//
//    @BeforeAll
//    static void initJfx() throws InterruptedException {
//        // ... (JFX init code remains the same)
//        if (Platform.isFxApplicationThread() || isPlatformRunning()) {
//            return;
//        }
//
//        CountDownLatch latch = new CountDownLatch(1);
//        try {
//            Platform.startup(latch::countDown);
//            latch.await(5, TimeUnit.SECONDS);
//        } catch (IllegalStateException e) {
//            System.err.println("JavaFX Platform was already initialized.");
//        }
//    }
//
//    private static boolean isPlatformRunning() {
//        try {
//            Platform.runLater(() -> {});
//            return true;
//        } catch (IllegalStateException e) {
//            return false;
//        }
//    }
//
//    @BeforeEach
//    void setUp() throws Exception {
//        mockStage = mock(Stage.class);
//        SceneManager.init(mockStage);
//
//        Field mainSceneField = SceneManager.class.getDeclaredField("mainScene");
//        mainSceneField.setAccessible(true);
//        mainSceneField.set(null, null);
//    }
//
//    @Test
//    void switchRoot_ShouldInitSceneApplyThemeAndShowStageOnFirstCall() throws PreferencesException {
//        try (MockedStatic<ThemeManager> mockedThemeManager = mockStatic(ThemeManager.class)) {
//            ThemeManager mockTheme = mock(ThemeManager.class);
//            mockedThemeManager.when(ThemeManager::getInstance).thenReturn(mockTheme);
//
//            // FIX: Use a real Parent object to allow 'new Scene(root)' to execute successfully
//            Parent realRoot = new javafx.scene.layout.StackPane();
//            realRoot.setUserData(TEST_FXML_PATH);
//            SceneManager.switchRoot(realRoot);
//
//            verify(mockStage, times(1)).setScene(any(Scene.class));
//            verify(mockStage, times(1)).show();
//            verify(mockStage, times(1)).setTitle("TestView - My Application");
//            verify(mockTheme, times(1)).applySavedTheme(any(Scene.class));
//            assertNotNull(SceneManager.getMainStage().getScene());
//        }
//    }
//
//    @Test
//    void switchRoot_ShouldReuseSceneAndApplyThemeOnSubsequentCall() throws PreferencesException, NoSuchFieldException, IllegalAccessException {
//        Scene mockScene = mock(Scene.class);
//        when(mockStage.getScene()).thenReturn(mockScene);
//
//        Field mainSceneField = SceneManager.class.getDeclaredField("mainScene");
//        mainSceneField.setAccessible(true);
//        mainSceneField.set(null, mockScene);
//
//        try (MockedStatic<ThemeManager> mockedThemeManager = mockStatic(ThemeManager.class)) {
//            ThemeManager mockTheme = mock(ThemeManager.class);
//            mockedThemeManager.when(ThemeManager::getInstance).thenReturn(mockTheme);
//
//            Parent newMockRoot = new Label();
//            newMockRoot.setUserData(TEST_FXML_PATH);
//            SceneManager.switchRoot(newMockRoot);
//            verify(mockScene, times(1)).setRoot(newMockRoot);
//
//            verify(mockStage, never()).setScene(any(Scene.class));
//            verify(mockStage, never()).show();
//
//            verify(mockTheme, times(1)).applySavedTheme(mockScene);
//        }
//    }
//
//    @Test
//    void switchRootFxmlPath_ShouldLoadFxmlAndSetTitle() throws IOException {
//        try (MockedStatic<LanguageManager> mockedLangManager = mockStatic(LanguageManager.class);
//             MockedStatic<AppContext> mockedAppContext = mockStatic(AppContext.class);
//             // Use a spy on SceneManager to mock the static getResource call
//             MockedStatic<SceneManager> mockedSceneManager = mockStatic(SceneManager.class, CALLS_REAL_METHODS);
//             MockedConstruction<FXMLLoader> mockedLoader = mockConstruction(FXMLLoader.class,
//                     (mock, context) -> {
//                         Parent dummyRoot = new Label();
//                         when(mock.load()).thenReturn(dummyRoot);
//                     }))
//        {
//            mockedLangManager.when(LanguageManager::getInstance).thenReturn(mock(LanguageManager.class));
//            mockedLangManager.when(() -> LanguageManager.getInstance().getBundle()).thenReturn(ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH));
//            mockedAppContext.when(AppContext::getInstance).thenReturn(mock(AppContext.class));
//
//            // FIX: Mock the internal call to getResource to return a non-null URL
//            mockedSceneManager.when(() -> SceneManager.class.getResource(TEST_FXML_PATH)).thenReturn(MOCKED_URL);
//
//            SceneManager.switchRoot(TEST_FXML_PATH);
//
//            FXMLLoader loader = mockedLoader.constructed().get(0);
//            verify(loader).setControllerFactory(any());
//            verify(loader).load();
//            // Verify interactions on the real/spied SceneManager (for title setting)
//            verify(mockStage).setTitle(contains("TestView"));
//        }
//    }
//
//    @Test
//    void switchRootFxmlPath_ShouldThrowSceneLoadExceptionOnIoError() {
//        try (MockedStatic<LanguageManager> mockedLangManager = mockStatic(LanguageManager.class);
//             MockedStatic<AppContext> mockedAppContext = mockStatic(AppContext.class);
//             // Use a spy on SceneManager to mock the static getResource call
//             MockedStatic<SceneManager> mockedSceneManager = mockStatic(SceneManager.class, CALLS_REAL_METHODS);
//             MockedConstruction<FXMLLoader> mockedLoader = mockConstruction(FXMLLoader.class,
//                     (mock, context) -> {
//                         when(mock.load()).thenThrow(new IOException("Simulated FXML error"));
//                     }))
//        {
//            mockedLangManager.when(LanguageManager::getInstance).thenReturn(mock(LanguageManager.class));
//            mockedLangManager.when(() -> LanguageManager.getInstance().getBundle()).thenReturn(ResourceBundle.getBundle("i18n.messages", Locale.ENGLISH));
//            mockedAppContext.when(AppContext::getInstance).thenReturn(mock(AppContext.class));
//
//            // FIX: Mock the internal call to getResource to return a non-null URL
//            mockedSceneManager.when(() -> SceneManager.class.getResource(TEST_FXML_PATH)).thenReturn(MOCKED_URL);
//
//            SceneLoadException ex = assertThrows(SceneLoadException.class, () ->
//                    SceneManager.switchRoot(TEST_FXML_PATH)
//            );
//
//            assertTrue(ex.getMessage().contains("Failed to load FXML"));
//            assertInstanceOf(IOException.class, ex.getCause());
//        }
//    }
//
//    @Test
//    void refreshCurrentRoot_ShouldCallSwitchRootWithCurrentPath() throws NoSuchFieldException, IllegalAccessException {
//        Scene mockScene = mock(Scene.class);
//        Parent mockRoot = mock(Parent.class);
//        when(mockScene.getRoot()).thenReturn(mockRoot);
//        when(mockRoot.getUserData()).thenReturn(TEST_FXML_PATH);
//
//        Field mainSceneField = SceneManager.class.getDeclaredField("mainScene");
//        mainSceneField.setAccessible(true);
//        mainSceneField.set(null, mockScene);
//
//        try (MockedStatic<SceneManager> mockedSceneManager = mockStatic(SceneManager.class, CALLS_REAL_METHODS)) {
//            when(mockStage.getScene()).thenReturn(mockScene);
//            SceneManager.init(mockStage);
//
//            // FINAL FIX: Use doAnswer for robust static void method stubbing
//            // This prevents the FXML loading code from running and resolves the compiler error.
//            Mockito.doAnswer(invocation -> null)
//                    .when(() -> SceneManager.switchRoot(TEST_FXML_PATH));
//
//            SceneManager.refreshCurrentRoot();
//
//            mockedSceneManager.verify(() -> SceneManager.switchRoot(TEST_FXML_PATH), times(1));
//        }
//    }
//}