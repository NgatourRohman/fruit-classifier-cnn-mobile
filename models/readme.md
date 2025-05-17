### 📁 MobileNetV2-based fruit classification models:

1. **`model_fruit_best.pth`**  
   - *PyTorch checkpoint with the best validation accuracy*  
   - Used for retraining, analysis, or evaluation.

2. **`model_fruit_final.pth`**  
   - *Final trained weights after all epochs*  
   - Represents the model state at the end of training.

3. **`model_fruit.pt`**  
   - *Standard TorchScript model*  
   - Exported for deployment (Android/iOS compatible).

4. **`model_fruit_android.pt`**  
   - *Optimized TorchScript model for Android*  
   - Quantized for smaller size and faster inference.

---

### 🚀 Usage Guide:
- **Training/Evaluation**: Use `.pth` files.  
- **Production**: Deploy `.pt` files to mobile apps.  
- **Android**: Prioritize `_android.pt` for optimized performance.