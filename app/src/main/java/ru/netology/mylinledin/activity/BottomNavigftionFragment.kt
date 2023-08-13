package ru.netology.mylinledin.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import ru.netology.mylinledin.R
import ru.netology.mylinledin.activity.event.EventFragment
import ru.netology.mylinledin.databinding.FragmentBottomNavigationBinding

class BottomNavigationFragment : Fragment() {

    private companion object {
        const val FEED_TAG = "FEED_TAG"
        const val JOB_TAG = "JOB_TAG"
        const val EVENT_TAG = "EVENT TAG"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentBottomNavigationBinding.inflate(inflater, container, false)

        // Стартовая вкладка – Feed, если фрагментов нет
        if (childFragmentManager.findFragmentById(R.id.container) == null) {
            loadFragment(FEED_TAG, ::FeedFragment)
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(FEED_TAG, ::FeedFragment)
                    true
                }

                R.id.job -> {
                    loadFragment(JOB_TAG, ::JobFragment)
                    true
                }

                R.id.event -> {
                    loadFragment(EVENT_TAG, ::EventFragment)
                    true
                }

                else -> false
            }
        }




        return binding.root
    }

    /**
     * Создаём фрагменты лениво и переиспользуем старые
     */
    private inline fun loadFragment(tag: String, fragmentFactory: () -> Fragment) {
        // Фрагмент, к которому мы хотим перейти. Его кешированная версия
        val cachedFragment = childFragmentManager.findFragmentByTag(tag)
        // Фрагмент, который сейчас на экране
        val currentFragment = childFragmentManager.findFragmentById(R.id.container)

        // При повторной навигации ничего не делаем
        if (currentFragment?.tag == tag) return

        childFragmentManager.commit {
            if (currentFragment != null) {
                // Старый фрагмент не теряем, а откладываем
                detach(currentFragment)
            }
            if (cachedFragment != null) {
                // Цепляем старый
                attach(cachedFragment)
            } else {
                // Создаём новый и присваиваем ему тег
                replace(R.id.container, fragmentFactory(), tag)
            }
        }
    }
}